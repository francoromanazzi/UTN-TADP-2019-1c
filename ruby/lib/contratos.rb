module Contratos
	# Definir comportamiento para ejecutar antes y después de recibir cada mensaje
	def before_and_after_each_call(proc_before, proc_after)

		# Definir un proc que agrega los procs a ejecutarse antes y después de un determinado método
		# Se va a usar tanto para los métodos ya existentes como para los métodos por definir
		add_before_and_after_procs = proc {|method_name|
			# Obtener implementación original del método
			original_method = instance_method(method_name)

			# Ver si este método en particular tería pre/post condiciones
			proc_precondition = @proc_precondition ? @procs_before.pop : nil
			proc_postcondition = @proc_postcondition ? @procs_after.pop : nil

			# Reset de pre/post condiciones
			@proc_precondition = false
			@proc_postcondition = false

			# Sobreescribir el metodo de la clase, para que ejecute los procs antes y después
			define_method(method_name) do |*args, &block|
				original_method_bounded = original_method.bind(self)

				# Si hay métodos que llaman otros métodos, evitar que los procs de antes y despues se vuelvan a ejecutar
				return original_method_bounded.(*args, &block) if caller.any? {|stack| stack.end_with?("before_and_after_each_call'")}

				# Agregar temporalmente args como variables de instancia de la clase para accederlos en el contexto de esta instancia
				# Además, me guardo los métodos que sobreescribo
				overwritten_methods = Hash.new
				args.each_with_index  do |arg, i|
					new_method_name = original_method.parameters[i][1]
					overwritten_methods[new_method_name] = self.class.instance_method(new_method_name).bind(self) if self.class.method_defined?(new_method_name)
					define_singleton_method(new_method_name) { arg }
				end

				# Invocar todos los procs a ejecutarse antes
				instance_eval(&proc_precondition) if proc_precondition
				self.class.instance_variable_get(:@procs_before).each {|proc| instance_eval(&proc)} unless method_name == :initialize

				# Invocar la implementación original del método en cuestión
				res = original_method_bounded.(*args, &block)

				# Invocar todos los procs a ejecutarse después
				instance_exec(res, &proc_postcondition) if proc_postcondition
				self.class.instance_variable_get(:@procs_after).each {|proc| instance_exec(res, &proc)}

				# Borrar los métodos args agregados temporalmente, y luego restaurar los anteriores que pudieron haber sido sobreescritos
				args.each_index  do |i|
					method_name = original_method.parameters[i][1]
					singleton_class.send :undef_method, method_name
				end
				overwritten_methods.each do |method_name, bound_method|
					define_singleton_method(method_name) {|*args, &block| bound_method.(*args, &block)}
				end

				# Retornar el resultado del método original
				res
			end
		}

		# Agrego los procs a una variable de instancia
		@procs_before ||= []
		@procs_after ||= []
		@procs_before << proc_before unless @proc_postcondition # Para no agregar el proc vacío
		@procs_after << proc_after unless @proc_precondition and not @proc_postcondition # Para no agregar el proc vacío. Podrían ser true/true (considero que primero escriben pre y despues escriben post)

		# Agregar procs a los metodos ya definidos previamente a menos que sea pre/post condicion
		instance_methods.each {|method_name| add_before_and_after_procs.call(method_name)} unless @proc_precondition or @proc_postcondition

		define_singleton_method(:method_added) do |method_name|
			# Evitar que method_added sea llamado infinitamente por el define_method
			return @break_recursivity = false if @break_recursivity ||= false

			@break_recursivity = true

			add_before_and_after_procs.call(method_name)
		end
	end

	def invariant(&block)
		before_and_after_each_call(proc {}, proc {
			raise "Failed to ensure invariant condition" unless instance_eval(&block)
		})
	end

	def pre(&block)
		raise "Precondition already defined" if @proc_precondition
		@proc_precondition = true
		before_and_after_each_call(proc {
			raise "Failed to meet preconditions" unless instance_eval(&block)
		}, proc {})
	end

	def post(&block)
		raise "Postcondition already defined" if @proc_postcondition
		@proc_postcondition = true
		before_and_after_each_call(proc {}, proc {
			|res| raise "Failed to meet postconditions" unless instance_exec(res, &block)
		})
	end
end







