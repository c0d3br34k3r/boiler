package au.com.codeka.carrot;

public interface CharQueue {

	int next();

	void unread(char c);

	int lineNumber();

	int columnNumber();

}
