class Pila
	attr_accessor :current_node, :capacity

	invariant { capacity >= 0}

	post { empty? }
	def initialize(capacity)
		@capacity = capacity
		@current_node = nil
	end

	pre { !full? }
	post { height > 0 }
	def push(element)
		pp "Soy push"
		@current_node = Node.new(element, @current_node)
	end

	pre {!empty?}
	def pop
		element = top
		@current_node = @current_node.next_node
		element
	end

	pre {!empty?}
	def top
		@current_node.element
	end

	def height
		empty? ? 0 : @current_node.size
	end

	def empty?
		@current_node.nil?
	end

	def full?
		height == capacity
	end

	Node = Struct.new(:element, :next_node) do
		def size
			next_node.nil? ? 1 : 1 + next_node.size
		end
	end
end

describe Pila do
	let(:pila) {described_class.new(2)}

	it 'debería no poder instanciarse con capacidad negativa' do
		expect{described_class.new(-1)}.to raise_error(RuntimeError, "Failed to ensure invariant condition")
	end

	it 'debería no poder hacer pop de la pila vacía' do
		expect{pila.pop}.to raise_error(RuntimeError, "Failed to meet preconditions")
	end

	it 'debería no poder exceder su capacidad máxima' do
		pila.push("1")
		pila.push("2")
		expect{pila.push("3")}.to raise_error(RuntimeError, "Failed to meet preconditions")
	end
end
