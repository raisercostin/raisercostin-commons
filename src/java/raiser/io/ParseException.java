package raiser.io;
public class ParseException extends Exception
{
    public ParseException()
    {
        super();
    }
    public ParseException(String arg0)
    {
        super(arg0);
    }
    public ParseException(String message,Exception detail)
    {
        super(message,detail);
    }
    public ParseException(Exception e)
    {
        super(e);
    }
}
