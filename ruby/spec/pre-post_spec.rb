class Operaciones
	pre { divisor != 0}
	post {|result| result * divisor == dividendo} # Por ej, no admite que 5/2 = 2 sea válido porque tendría resto
	def division_entera_sin_resto(dividendo, divisor)
		dividendo / divisor
	end

	def division_entera_con_resto(dividendo, divisor)
		dividendo / divisor
	end
end

describe '#pre-post-conditions' do
	let(:op) {Operaciones.new}

	it 'debería fallar si una precondición no se cumple' do
		expect{op.division_entera_sin_resto(4, 0)}.to raise_error(RuntimeError, "Failed to meet preconditions")
	end

	it 'debería fallar si una postcondicion no se cumple' do
		expect{op.division_entera_sin_resto(4, 3)}.to raise_error(RuntimeError, "Failed to meet postconditions")
	end

	it 'debería no fallar si se cumplen las pre y post condiciones' do
		expect(op.division_entera_sin_resto(4, 2)).to be 2
	end

	it 'debería no afectar los otros métodos que no tienen pre/post condiciones' do
		pp op.division_entera_sin_resto(4, 2)
		expect(op.division_entera_con_resto(4, 3)).to be 1
	end

	it 'debería no poder definir más de dos precondiciones a un método' do
	  expect{
		  class ClaseTest
			  pre{x > 0}
			  pre{x % 2 == 0}
			  def foo(x)
				  x * x
			  end
		  end
	  }.to raise_error(RuntimeError, "Precondition already defined")
	end

	it 'debería no poder definir más de dos postcondiciones a un método' do
		expect{
			class ClaseTest2
				post{|res| res > 0}
				post{|res| res % 2 == 0}
				def foo(x)
					x * x
				end
			end
		}.to raise_error(RuntimeError, "Postcondition already defined")
	end

	it 'debería poder definir la postcondicion antes de la precondicion' do
		class ClaseTest3
			post{|res| res < 0}
			pre{x > 0}
			def foo(x)
				x * x
			end
		end
		expect{ClaseTest3.new.foo(4)}.to raise_error(RuntimeError, "Failed to meet postconditions")
	end

	it 'debería no pisar una variable de instancia del mismo nombre que un parámetro' do

		class ClaseTest4
			attr_accessor :var_instancia

			def initialize
				@var_instancia = 555
			end

			post{|res| res > var_instancia }
			def foo(var_instancia)
				var_instancia * var_instancia
			end
		end

		clase_test4 = ClaseTest4.new

		clase_test4.foo(4)

		expect(clase_test4.var_instancia).to be 555
	end
end
