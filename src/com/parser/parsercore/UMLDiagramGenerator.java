package com.parser.parsercore;

import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

import com.parser.beans.Collection_Unit;
import com.parser.beans.Edge;
import com.parser.beans.Unit;

public class UMLDiagramGenerator {
		
	public void generateSyntax(Collection_Unit cu, File folder,String outputName)
	{
		
		String output = "@startuml\n";
		List<Unit>units = cu.getUnits();
		Iterator<Unit> iterator = units.iterator();
		while(iterator.hasNext())
		{
			Unit unit = iterator.next();
			List<MethodDeclaration> listOfMethods = unit.getMethods();
			//variableWithSetterGetter(listOfMethods);
			if(unit.isCheckInterface())
			{
				output += "interface "+unit.getClassName()+" << interface >> {\n";
			}
			else
			{
				output +="class "+unit.getClassName()+" {\n";
			}
			List<FieldDeclaration>listOfVariables = unit.getVariables();
			List<MethodDeclaration> list = setterAndGetterRemoval(listOfMethods,listOfVariables);
			for (MethodDeclaration methodDeclaration : list) {
				listOfMethods.remove(methodDeclaration);
			}
			
			
			for (MethodDeclaration method : listOfMethods)
			{
				int intModifier = method.getModifiers();
				String modifier = Modifier.toString(intModifier);
				if(modifier.equals("public"))
				{
					List<Parameter> parameters=method.getParameters();
					if(parameters!=null)
					{
						for(Parameter parameter : parameters)
						{
							output += "+"+method.getName()+"("+parameter.getId().toString()+" : "+parameter.getType()+") :"+method.getType()+"\n";
						}
						
					}
					else
					{	
						output += "+"+method.getName()+"() :"+method.getType()+"\n";
					}
				}
				
				else if(intModifier !=1 && intModifier != 1025 && intModifier == 9)
				{
					List<Parameter> parameters=method.getParameters();
					if(parameters != null )
					{
						for (Parameter parameter2 : parameters)
						{
							if(parameter2.getType()!=null)
							{
								output += "{static}+"+method.getName()+"("+
										parameter2.getId().toString()+" : "+parameter2.getType()+") :"+method.getType()+"\n";
							}
						}
					}
					
				}
				else if(intModifier == 1025)
				{
					output += "+"+method.getName()+"() :"+method.getType()+"\n";
				}
				
			}
			
			for (FieldDeclaration variable : listOfVariables)
			{
				int intModifier = variable.getModifiers();
				String modifier = Modifier.toString(intModifier);
				String var = variable.getVariables().toString();
				var = var.replace("[", "");
				var = var.replace("]", "");
				if(modifier.equals("public"))
				{
					
					output += "+"+var+ " : "+variable.getType()+"\n";
				}
				else if(modifier.equals("private"))
				{
					output += "-"+var+ " : "+variable.getType()+"\n";
				}	
				
			}
			
			output = constructorMethods(output,unit);
			
			
		
			
			output +="}\n";
		}
		
		Iterator<Edge> iterator1 = cu.getEdge().iterator();
		while(iterator1.hasNext())
		{
			Edge edge = iterator1.next();
			output = generateEdges(output, cu, edge);
		}
		output += "@enduml";
		System.out.println("-----------------------------------");
		//System.out.println(output);
		
		
		System.out.println(output);
		generateUMLDiagram(output,folder,outputName);
	}

	
	
	
	
	private String constructorMethods(String output,Unit unit)
	{
		List<ConstructorDeclaration>constructorList = unit.getConstructors();
		for (ConstructorDeclaration constructor : constructorList) 
		{
			List<Parameter>parameters = constructor.getParameters();
			if(parameters!= null)
			{	
				for (Parameter parameter : parameters) 
				{
					if(parameter.getType()!=null)
					{
						output += "+"+constructor.getName()+"("+
								parameter.getId().toString()+" : "+parameter.getType()+")\n";
					}
				}
			}
			else
			{
				output += "+"+constructor.getName()+"()\n";

			}
		}
		return output;
	}

	private void generateUMLDiagram(String output, File folder,String outputName) 
	{
		SourceStringReader ssr =  new SourceStringReader(output);
		try
		{
			FileOutputStream fos = new FileOutputStream(outputName+".png");
			ssr.generateImage(fos,  new FileFormatOption(FileFormat.PNG, false));
		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public List<MethodDeclaration> setterAndGetterRemoval(List<MethodDeclaration> listOfMethods,List<FieldDeclaration> listOfVariables)
	{
		List<MethodDeclaration> list = new ArrayList<MethodDeclaration>();
		for (MethodDeclaration method : listOfMethods)
		{
			for(FieldDeclaration variable : listOfVariables)
			{
				String variableName = "get"+variable.getVariables().get(0).toString();
				String variableName1 = "set"+variable.getVariables().get(0).toString();
				if( (method.getName().equalsIgnoreCase(variableName1) || method.getName().equalsIgnoreCase(variableName)))
				{
					list.add(method);
					variable.setModifiers(1);
				}
			}
		}
		return list;
	}
	public List<String> variableWithSetterGetter(List<MethodDeclaration> listOfMethods)
	{
		String name = null;
		List<String> result = new ArrayList<String>();
		for (MethodDeclaration method : listOfMethods) 
		{
			if(method.getName().startsWith("set"))
			{
				name = method.getName();
				name = name.substring(3);
				for (MethodDeclaration method1 : listOfMethods)
				{
					if(method1.getName().startsWith("get") && method1.getName().contains(name))
					{
						result.add(name.toLowerCase());
					}
				}
			}
		}
		return result;
	}
	
	public String generateEdges(String output, Collection_Unit cu,Edge edge)
	{
		if(edge.getType() != null)
		{
			if(edge.getType().toString().equals("EXTENDS"))
			{
				output += edge.getDestination().getClassName()+" <|-- "+edge.getSource().getClassName()+"\n";
			}
			else if(edge.getType().toString().equals("IMPLEMENTS"))
			{
				output += edge.getDestination().getClassName()+" <|.. "+edge.getSource().getClassName()+"\n";
			}
			else if(edge.getType().toString().equals("ASSOCIATION"))
			{
				if(edge.getCardinality() !=null)
				{
					output += edge.getDestination().getClassName()+" \""+edge.getCardinality()+"\""+" -- "+"\"1\" "+edge.getSource().getClassName()+"\n";
				}
				else
				{
					output += edge.getDestination().getClassName()+" -- "+edge.getSource().getClassName()+"\n";
				}
			}
			else if(edge.getType().toString().equals("DEPENDENCY"))
			{
				if(edge.getCardinality() !=null)
				{
					output += edge.getSource().getClassName() +" ..> "+edge.getDestination().getClassName()+"\n";
				}
				else
				{
					output += edge.getSource().getClassName() +" ..> "+edge.getDestination().getClassName()+"\n";

				}
			}
		}
		return output;
	}
	
}
