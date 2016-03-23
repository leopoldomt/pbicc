package cfp;

import icc.data.ICCLinkFindingResults;
import icc.data.VarInfo;
import icc.visitors.CFPVisitor;
import icc.visitors.SymbolTableVisitor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;

public class MainSymbolTableVisitor {
	
	public static void main(String[] args) throws FileNotFoundException, ParseException {
		List<String> l = Arrays.asList("test-data/explicit_intent_test/app/src/main/java/br/ufpe/cin/pbicc/test/intents/explicit/MainActivity.java","test-data/explicit_intent_test/app/src/main/java/br/ufpe/cin/pbicc/test/intents/explicit/Strings.java");		
		ICCLinkFindingResults results = new ICCLinkFindingResults();
		/**/
		
		CFPVisitor visitor = new CFPVisitor(results);
		for (String s : l) {
			FileInputStream in = new FileInputStream(s);
			visitor = new CFPVisitor(results);
			CompilationUnit cu = JavaParser.parse(in);
			cu.accept(visitor, null);	
		}
		/*
		for (Map.Entry<String, String> keyValue : results.propagate().entrySet()) {
			System.out.printf("%s -> %s\n", keyValue.getKey(), keyValue.getValue());
		}
		/**/
		results.propagate();

		/**/
		/**/
		SymbolTableVisitor stVisitor = new SymbolTableVisitor(results);
		
		for (String s : l) {
			FileInputStream in = new FileInputStream(s);
			stVisitor = new SymbolTableVisitor(results);
			CompilationUnit cu = JavaParser.parse(in);
			cu.accept(stVisitor, null);	
		}
				
		for (Map.Entry<String, VarInfo> keyValue : results.varsST.getMap().entrySet()) {
			System.out.printf("%s -> %s\n", keyValue.getKey(), keyValue.getValue().toString());
		}
		/**/


	}

}