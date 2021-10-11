import java.util.ArrayList;
import java.util.Iterator;

public class LogicAsControlFlowSample {
	
	public void firstMethod(){
		int a = 1;
		int b = 5;
		
		if(++a > 0 && ++b > 0) {
			a = a * 2;
			b = b * 2;
		}
		
		System.out.println(a + " " + b);
	}
	
	public void firstTransformedMethod() {
		int a = 1;
		int b = 5;
		
		if(++a > 0) {
			a = a * 2;
			b = b * 2;
		} else if(++b > 0) {
			a = a * 2;
			b = b * 2;
		}
		
		System.out.println(a + " " + b);
	}
	
	public void secondMethod(){
		int a = 1;
		int b = 5;
		
		boolean test = a == b && ++a > 0 || ++b > 0;
		
		System.out.println(a + " " + b);
	}
	
	public void secondTransformedMethod(){
		int a = 1;
		int b = 5;
		
		if(a == b) {
			++a;
		} else {
			++b;
		}
		
		System.out.println(a + " " + b);
	}
	
	public void thirdMethod(){
		int a = 1;
		int b = 5;
		int c = 0;
		
		while(c < b && ++a > 0) {
			c++;
		}
		
		System.out.println(a + " " + b + " " + c);
	}
	
	public void thirdTransformedMethod(){
		int a = 1;
		int b = 5;
		int c = 0;
		
		while(a != b) {
			a++;
			if(!(a > 0)) {
				break;
			}		
			c++;
		}
		
		System.out.println(a + " " + b + " " + c);
	}
	
}
