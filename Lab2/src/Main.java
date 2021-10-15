public class Main {
    public static void main(String[] args) {
        SymbolTable symbolTable = new SymbolTable(20);

        /*
        Example of a program to test the symbol table:

        PROGRAM
	    MAIN -> {
		    DECLARATIONS
			    INTEGER: nr_1, nr_2, min, random_no=1, random_string="hello", no;
			    STRING: output_message <- "The minimum of the 3 numbers is: ";
		    STATEMENTS
		    {
			    in>>nr_1, nr_2, nr_3;
			    no <- 1;
			    if (nr_1 < nr_2) then
				    min <- nr_1;
			    else
				    min <- nr_2;
			    out<<output_message, min;
		    }
	    }
         */

        String[] identifiers = {"nr_1", "nr_2", "min", "output_message","random_no","random_string","no"};
        String[] constants = {"\"hello\"", "1"};

        symbolTable.insertSymbol("nr_1");
        symbolTable.insertSymbol("nr_2");
        symbolTable.insertSymbol("min");
        symbolTable.insertSymbol("output_message");
        symbolTable.insertSymbol("random_no");
        symbolTable.insertSymbol("1");
        symbolTable.insertSymbol("random_string");
        symbolTable.insertSymbol("\"hello\"");
        symbolTable.insertSymbol("no");

        System.out.println(symbolTable);

        int sum_ascii = ((int)'n') + ((int)'r') + ((int)'_') + ((int)'1');

        System.out.println(symbolTable.getSymbolPosition("nr_1"));
        if(symbolTable.getSymbolPosition("nr_1").getFirst() != sum_ascii % 20)
            System.out.println("Wrong hashing");
        for(int i = 0; i< identifiers.length; i++) {
            if(symbolTable.getSymbolPosition(identifiers[i]) == null) {
                System.out.println("Could not find identifier: " + identifiers[i]);
            }
        }

        for(int i = 0; i< constants.length; i++) {
            if(symbolTable.getSymbolPosition(constants[i]) == null) {
                System.out.println("Could not find constant: " + constants[i]);
            }
        }

        if(symbolTable.insertSymbol("nr_1"))
            System.out.println("Inserting again identifier nr_1 failed to reject");
        if(symbolTable.insertSymbol("1"))
            System.out.println("Inserting again constant \"1\" failed to reject");

        if(symbolTable.getSymbolPosition("not_existing") != null)
            System.out.println("Searching a non existing symbol failed");

    }
}
