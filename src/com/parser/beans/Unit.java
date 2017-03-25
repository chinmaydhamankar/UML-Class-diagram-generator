package com.parser.beans;

import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;

import java.util.ArrayList;
import java.util.List;

public class Unit {
	private String className;
	private List<FieldDeclaration> variables;
	private List<MethodDeclaration> methods;
	private List<ConstructorDeclaration> constructors;
	private boolean checkInterface;
	private int modifier;
	
	
	public int getModifier() {
		return modifier;
	}

	public void setModifier(int modifier) {
		this.modifier = modifier;
	}
	

	public boolean isCheckInterface() {
		return checkInterface;
	}

	public void setCheckInterface(boolean checkInterface) {
		this.checkInterface = checkInterface;
	}

	public Unit(){
		this.variables = new ArrayList<FieldDeclaration>(0);
		this.methods = new ArrayList<MethodDeclaration>(0);
		this.constructors = new ArrayList<ConstructorDeclaration>(0);
	}
	
	public List<ConstructorDeclaration> getConstructors() {
		return constructors;
	}

	public void setConstructors(List<ConstructorDeclaration> constructors) {
		this.constructors = constructors;
	}

	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public List<FieldDeclaration> getVariables() {
		return variables;
	}
	public void setVariables(List<FieldDeclaration> variables) {
		this.variables = variables;
	}
	public List<MethodDeclaration> getMethods() {
		return methods;
	}
	public void setMethods(List<MethodDeclaration> methods) {
		this.methods = methods;
	}
}
