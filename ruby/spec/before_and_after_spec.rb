class Clase1
	attr_accessor :contador

	def metodo_definido_anteriormente
		"metodo_definido_anteriormente"
	end

	before_and_after_each_call(proc {puts "procBefore. Contador: #{incrementar}"}, proc {|res| pp "procAfter: #{res.to_s}"})

	def initialize
		pp "soy initialize"
		@contador = 0
	end

	def incrementar
		puts "Soy incrementar"
		@contador += 1
	end

	def metodo_que_llama_otro_metodo
		puts foo
		"metodo_que_llama_otro_metodo"
	end

	def foo
		"foo"
	end
end

describe '#before_and_after_each_call' do
	let(:clase1) {Clase1.new}

	it 'debería ejecutar los procs antes y después de cada mensaje' do
		puts clase1.foo
		expect(clase1.contador).to be 2 # 2 ya que le mandé 2 mensajes
	end

	it 'debería agregar los procs a los métodos ya definidos anteriormente' do
		puts clase1.metodo_definido_anteriormente
		expect(clase1.contador).to be 2 # 2 ya que le mandé 2 mensajes
	end

	it 'debería no llamar a los procs al llamar métodos dentro del método del mensaje enviado' do
		puts clase1.metodo_que_llama_otro_metodo
		expect(clase1.contador).to be 2 # 2 ya que le mandé 2 mensajes (foo no debería contar)
	end

	it 'debería poder agregar los procs a los métodos añadidos al volver a abrir la clase' do
		Clase1.class_eval {define_method(:metodo_definido_a_posteriori) {
			"metodo_definido_a_posteriori"
		}}

		puts clase1.metodo_definido_a_posteriori
		expect(clase1.contador).to be 2 # 2 ya que le mandé 2 mensajes

		Clase1.remove_method(:metodo_definido_a_posteriori)
	end

	it 'debería poder agregar más de un before_and_after_each_call' do
		class Clase1_v2
			attr_accessor :contador

			def initialize
				@contador = 0
			end

			before_and_after_each_call(proc {puts "procBefore1. Contador: #{incrementar}"}, proc {puts "procAfter1"})

			def incrementar
				puts "Soy incrementar"
				@contador += 1
			end

			def foo
				"foo"
			end
		end

		class Clase1_v2
			before_and_after_each_call(proc {puts "procBefore2. Contador: #{incrementar}"}, proc {puts "procAfter2"})
		end

		clase2 = Clase1_v2.new

		puts clase2.foo
		expect(clase2.contador).to be 4 # 4 ya que le mandé 2 mensajes, cada uno incrementando dos veces
	end

	it 'debería no afectar los métodos de clase' do
		Clase1.class_eval {define_singleton_method(:metodo_de_clase) {
			"metodo_de_clase"
		}}

		puts Clase1.metodo_de_clase
		expect(clase1.contador).to be 1 # 1 ya que el mensaje metodo_de_clase no cuenta (solo cuenta el mensaje contador)

		class << Clase1
			remove_method(:metodo_de_clase)
		end
	end
end