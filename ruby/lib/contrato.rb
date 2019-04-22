class Contrato
	@@pres, @@posts = [], []
	@@setup = Proc.new { @@pres.each { |p| p.call } }
	@@tear_down = Proc.new { @@posts.each { |p| p.call } }

	def self.before_and_after_each_call(pre, post)
		@@pres.push(pre)
		@@posts.push(post)
	end

	def self.define_method(name, &block)
		super(name) do
			@@setup.call
			block.call
			@@tear_down.call
		end
	end
end