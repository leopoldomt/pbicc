package cfp;

class A {

	static String x = "Hello";
	static String y = B.x;
	static String z = x + "World";
	static String w = x + B.x;
	
	void foo() {
		String teste;
		String b;
		b = "depois";
		System.out.println(b);
	}
}
