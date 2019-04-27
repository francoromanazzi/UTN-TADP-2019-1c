class Guerrero
	attr_accessor :vida

	invariant {vida >= 0}

	def initialize
		@vida = 100
	end

	def recibir_danio(danio)
		@vida -= danio
	end
end

describe '#invariant' do
	let(:un_guerrero) {Guerrero.new}

	it 'debería fallar si un mensaje provoca un incumplimiento de la condición' do
		expect{un_guerrero.recibir_danio(500)}.to raise_error(RuntimeError, "Failed to ensure invariant condition")
	end

	it 'debería no fallar si un mensaje no provoca un incumplimiento de la condición' do
		un_guerrero.recibir_danio(15)
		expect(un_guerrero.vida).to be Guerrero.new.vida - 15
	end

	it 'debería poder definir más de un invariant sobre una variable' do
		class Guerrero_v2
			attr_accessor :vida

			invariant { vida >= 0 }
			invariant { vida <= 100 }

			def initialize
				@vida = 100
			end

			def recibir_danio(danio)
				@vida -= danio
			end

			def curarse(curacion)
				@vida += curacion
			end
		end

		otro_guerrero = Guerrero_v2.new
		expect{otro_guerrero.curarse(20)}.to raise_error(RuntimeError, "Failed to ensure invariant condition")
		expect{otro_guerrero.recibir_danio(200)}.to raise_error(RuntimeError, "Failed to ensure invariant condition")
	end

	it 'debería funcionar también en el initialize' do
		class Guerrero_v3
			attr_accessor :vida

			invariant { vida >= 0 }

			def initialize(vida)
				@vida = vida
			end
		end

		expect{Guerrero_v3.new(-20)}.to raise_error(RuntimeError, "Failed to ensure invariant condition")
	end
end