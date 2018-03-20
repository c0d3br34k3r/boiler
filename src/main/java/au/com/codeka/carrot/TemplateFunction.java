package au.com.codeka.carrot;

public interface TemplateFunction {

	Object apply(Params params);
	
	String name();

}
