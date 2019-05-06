class Module
	def contracts
		@contracts ||= []
	end

	def valid_before_and_after_contracts(method_name)
		valid_contracts_by_exec_moment = proc do |exec_moment|
			contracts.filter {|contract| contract.exec_moment == exec_moment && contract.valid_with_method?(method_name)}
		end

		valid_before_contracts = valid_contracts_by_exec_moment.call(:before)
		valid_after_contracts = valid_contracts_by_exec_moment.call(:after)
		[valid_before_contracts, valid_after_contracts]
	end

	def override_method(method_name)
		original_method = instance_method(method_name)

		define_method(method_name) do |*args, &block|
			original_method_bounded = original_method.bind(self)

			# No ejecutar los contratos de los métodos que fueron llamados por otros métodos (ya que no fueron los receptores del mensaje)
			return original_method_bounded.(*args, &block) if caller.any? {|stack| Regexp.new('^' + __FILE__).match(stack)}

			contracts_before, contracts_after = self.class.valid_before_and_after_contracts(method_name)

			# Agregar temporalmente args como getters en la autoclase del objeto para poder accederlos en el contexto de esta instancia
			# Además, me guardo los métodos que sobreescribo
			overwritten_methods = Hash.new
			args.each_with_index do |arg, i|
				new_method_name = original_method.parameters[i][1]
				overwritten_methods[new_method_name] = self.class.instance_method(new_method_name).bind(self) if self.class.method_defined?(new_method_name)
				define_singleton_method(new_method_name) {arg}
			end

			contracts_before.each {|contract| contract.exec(self)} unless method_name == :initialize

			res = original_method_bounded.(*args, &block)

			contracts_after.each {|contract| contract.exec(self, res)}

			# Borrar los métodos args agregados temporalmente en la autoclase del objeto
			args.each_index do |i|
				method_name_arg = original_method.parameters[i][1]
				singleton_class.send :undef_method, method_name_arg
			end
			# Restaurar los métodos anteriores que pudieron haber sido sobreescritos
			overwritten_methods.each do |method_name, bound_method|
				define_singleton_method(method_name) {|*args, &block| bound_method.(*args, &block)}
			end

			res
		end
	end

	def bind_unbounded_method_contracts(method_name)
		@contracts = contracts.map {|contract| contract.is_a?(UnboundMethodContract) ? contract.bind_to_method(method_name) : contract}
	end

	def method_added(method_name)
		return if contracts.empty?

		# Evitar que method_added sea llamado infinitamente por el define_method
		return @break_recursitivy = false if @break_recursitivy ||= false
		@break_recursitivy = true

		# Si había pre/post esperando a que se defina el método, bindearselo
		bind_unbounded_method_contracts(method_name)

		override_method(method_name)
	end

	def override_existing_methods
		instance_methods.each {|method_name| override_method(method_name)}
	end

	def before_and_after_each_call(proc_before, proc_after)
		contracts << GlobalContract.new(:before, proc_before)
		contracts << GlobalContract.new(:after, proc_after)
		override_existing_methods
	end

	def invariant(&block)
		contracts << GlobalContract.new(:after, proc {raise "Failed to ensure invariant condition" unless instance_eval(&block)})
		override_existing_methods
	end

	def pre(&block)
		raise "Precondition already defined" if contracts.any? {|contract| contract.is_a?(UnboundMethodContract) and contract.exec_moment == :before}
		contracts << UnboundMethodContract.new(:before, proc {raise "Failed to meet preconditions" unless instance_eval(&block)})
	end

	def post(&block)
		raise "Postcondition already defined" if contracts.any? {|contract| contract.is_a?(UnboundMethodContract) and contract.exec_moment == :after}
		contracts << UnboundMethodContract.new(:after, proc {|res| raise "Failed to meet postconditions" unless instance_exec(res, &block)})
	end
end

class Contract # abstract
	def initialize(exec_moment, proc)
		@proc = proc
		@exec_moment = exec_moment
	end

	def exec_moment
		@exec_moment
	end

	def exec(instance, *args)
		instance.instance_exec(*args, &@proc)
	end

	# abstract def valid_with_method?(method_name)
end

class GlobalContract < Contract
	def valid_with_method?(_)
		true
	end
end

class UnboundMethodContract < Contract
	def valid_with_method?(_)
		false
	end

	def bind_to_method(method_name)
		MethodContract.new(@exec_moment, @proc, method_name)
	end
end

class MethodContract < Contract
	def initialize(exec_moment, proc, method_name)
		super(exec_moment, proc)
		@method_name = method_name
	end

	def valid_with_method?(method_name)
		@method_name == method_name
	end
end