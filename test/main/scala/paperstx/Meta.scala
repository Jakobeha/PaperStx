package paperstx

import utest._

class Meta extends TestSuite {
  def tests = TestSuite {
    'General {
      assert(2 + 2 == 4)
    }
  }
}
