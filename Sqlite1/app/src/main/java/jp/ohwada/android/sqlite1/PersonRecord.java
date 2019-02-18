/**
 * SQLite Sample
 * 2019-02-01 K.OHWADA 
 */

package jp.ohwada.android.sqlite1;

/**
 * class PersonRecord
 */
public class PersonRecord {

	/** items */
	public int id = 0;
	public String name = "";
	public int age = 0;



    /**
     * === constractor === 
     */	
	public PersonRecord() {
		initRecord( 0, "", 0 );
	} // PersonRecord

    /**
     * === constractor ===  
     */	
	public PersonRecord( String name, String str_age ) {
        int int_age = parseInt(str_age);
		initRecord( 0, name, int_age );
	} // PersonRecord

    /**
     * === constractor ===  
     */	
	public PersonRecord( int id, String name, int age ) {
		initRecord( id, name,  age );
	} // PersonRecord


    /**
     * initRecord
     */	
	private void initRecord( int _id, String _name, int _age ) {
		id = _id;
		name = _name;	
		age = _age;	
	} // initRecord

public void setId( String _id ) {
    id =  parseInt( _id );
} 

public String getId() {
    return toString( id );
}

public void setAge( String _age ) {
    age =  parseInt( _age );
} 

public String getAge() {
    return toString( age );
}

public void setName( String _name ) {
    name = _name;
}

public String getName() {
    return name;
}

public String getMessage() {
   String str = getId() + " : " + getName() + " " + getAge();
    return str;
}

 	/**
	 * parseInt
	 */ 
private  int parseInt(String str) {
    int num = 0;
    try {
        num = Integer.parseInt( str );
    } catch (Exception e){
		e.printStackTrace();
    }
    return num;
} // parseInt

 	/**
	 * toString
	 */ 
private String toString( int num) {
    return Integer.toString( num );
}  // toString
	
} // class personRecord
