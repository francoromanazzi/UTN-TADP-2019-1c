describe Contrato do
  let(:contrato) { Contrato.new }
  let(:pre) { Proc.new { print "A" } }
  let(:post) { Proc.new { print "C" } }

  describe '#prePost' do
    it 'debería pasar este test' do
		contrato.before_and_after_each_call(pre, post)
		contrato.define_method(:test) { print "B" }
		expect(contrato.test).to be "ABC"
    end
  end
end