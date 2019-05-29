package mk.learning.utilities;

public class IOHelperFactory {

	public static IOHelper ioHelper=null;
	
	public static IOHelper getIOHelper() {
		if(ioHelper==null) {
			ioHelper = new IOHelper();
		}
		return ioHelper;
	}
	
}
