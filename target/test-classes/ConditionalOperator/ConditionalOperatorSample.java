
public class ConditionalOperatorSample {

	public ConditionalOperatorSample() {

	}

	public void firstMethod() {
		int a = 4;
		
		int b = a == 3 ? 2 : 1;
		
		System.out.println(b);
	}
	
	public String firstTransformedMethod() {
		int a = 4;
		int b = 3;
		int c;
		
		if(a == 3) {
			c = 2;
		} else {
			c = 1;
		}
		
		System.out.println(c);
	}

	public String secondMethod() {
		int a = 3;
		int b = 5;
		int c = 2;
		
		int d = a == 3 ? b : c;
		
		System.out.println(d);
	}

	public String secondTransformedMethod() {
		int a = 3;
		int b = 5;
		int c = 2;
		
		int d;
		if(a == 2) {
			d = b;
		} else {
			d = c;
		}
		
		System.out.println(d);
	}
	
	public String thirdMethod() {
		int a = 2;
		int b = 3;
		int c = 1;
		
		int d = a == 3 ? b : c;
		
		System.out.println(d);
	}
	
	public String thirdTransformedMethod() {
		int a = 2;
		int b = 3;
		int c = 1;
		
		int d;
		if(a == 3) {
			d = b;
		} else {
			d = c;
		}
		
		System.out.println(d);
	}
}
