class Guerrero
	attr_accessor :vida

	def initialize
		@vida = 100
	end

	typed({nombre: String}, String)
	def saludar(nombre)
		"Hola #{nombre}"
	end

	typed({}, Fixnum)
	def retorno_erroneo
		'Hola'
	end

	def recibir_danio(danio)
		@vida -= danio
		nil
	end

	typed({otro_guerrero: Guerrero, danio: Object}, NilClass)
	def atacar(otro_guerrero, danio)
		otro_guerrero.recibir_danio(danio)
	end
end

class StringLoco < String
	def sarasa
		self * 2
	end
end


describe '#typed' do
	let(:un_guerrero) {Guerrero.new}

	it 'debería funcionar con los tipos correctos' do
		expect(un_guerrero.saludar("Juan")).to eq "Hola Juan"
	end

	it 'debería fallar con un tipo de parametro erroneo' do
		expect{un_guerrero.saludar(15)}.to raise_error(RuntimeError, "Failed to meet preconditions")
	end

	it 'debería fallar con un retorno erroneo' do
		expect{un_guerrero.retorno_erroneo}.to raise_error(RuntimeError, "Failed to meet postconditions")
	end

	it 'debería funcionar con tipos que son subclases' do
		expect(un_guerrero.saludar(StringLoco.new('A'))).to eq 'Hola A'
	end

	it 'debería poder revolver nil' do
		otro = Guerrero.new
		un_guerrero.atacar(otro, 25)
		expect(otro.vida).to eq(100-25)
	end
end
