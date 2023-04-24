package symboltable.entities;

import java.util.ArrayList;
import java.util.List;

import symboltable.Scope;

public class EntityFactory {
	public static String MAIN_FUNC = "main_func";
	public static String LOCAL_FUNC = "local_func";
	public static String PARAMETER = "parameter";
	public static String FORMAL_PARAMETER = "formal_parameter";
	public static String VARIABLE = "variable";
	public static String TEMP_VARIABLE= "temp_variable";
	
	
	public static Entity createEntity(String objType, String[] args, Scope lastElement) {
		if (args[0].equals(MAIN_FUNC)) {
			return new MainFunction(args[1]);
		} else if (args[0].equals(LOCAL_FUNC)) {
			return new LocalFunction(args[1]);
		}
		
		
		return null;
		
	}
	
	public static Entity createFormalParameter(String name, ParameterMode mode) {
		return new FormalParameter(name, mode);
	}
	
	public static Entity createParameter(String name, ParameterMode mode) {
		return new Parameter(name, mode);
	}
	
	public static Entity createVariable(String name, int offset) {
		return new Variable(name, offset);
	}
	
	public static Entity createTemporaryVariable(String name, int offset) {
		return new TemporaryVariable(name, offset);
	}
	
	public static Function createLocalFunction(String name) {
		return new LocalFunction(name);
	}
	
	public static Function createLocalFunction(String name, List<String> formalParamsNames){
		List<FormalParameter> formalParams = new ArrayList<FormalParameter>();
		for (String fpName : formalParamsNames) {
			formalParams.add(new FormalParameter(fpName));
		}
		return new LocalFunction(name, formalParams);
	}
	 
	//	TODO fix this ugly hack
	public static List<Parameter> createListOfParameters(List<String> paramsNames) {
		List<Parameter> params = new ArrayList<Parameter>();
		for (int i = 0; i < paramsNames.size(); i++) {
			params.add(new Parameter(paramsNames.get(i), 12+4*i));
		}
		return params;
	}
	
	public static Function createMainFunction(String name) {
		return new MainFunction(name);
	}

	
	
}
