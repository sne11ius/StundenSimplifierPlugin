package nu.wasis.stunden.plugins.simplifier.exception;

public class ImpossibleSimplificationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ImpossibleSimplificationException() {
	}

	public ImpossibleSimplificationException(String arg0) {
		super(arg0);
	}

	public ImpossibleSimplificationException(Throwable arg0) {
		super(arg0);
	}

	public ImpossibleSimplificationException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public ImpossibleSimplificationException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

}
