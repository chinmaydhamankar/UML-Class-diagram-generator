package com.parser.parsercore;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.type.PrimitiveType;
import japa.parser.ast.type.ReferenceType;
import japa.parser.ast.type.Type;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.parser.beans.Collection_Unit;
import com.parser.beans.Edge;
import com.parser.beans.EdgeTypes;
import com.parser.beans.Unit;

public class ParseCore {

	public Collection_Unit scanAndParseFile(List<File> files) throws ParseException,IOException 
	{
		CompilationUnit compileUnit = new CompilationUnit();
		Collection_Unit collection = new Collection_Unit();
		HashMap<String, CompilationUnit> compilations = new HashMap<String, CompilationUnit>();
		
	for(File file : files)
		{
			compileUnit = JavaParser.parse(file);
			compilations.put(file.getName(), compileUnit);
			List<TypeDeclaration> list = compileUnit.getTypes();
			Iterator<TypeDeclaration> itr = list.iterator();
			while (itr.hasNext())
			{
				TypeDeclaration element = itr.next();
				Unit unit = new Unit();
				if(((ClassOrInterfaceDeclaration) element).isInterface())
				{
					String className = element.getName();
					unit.setClassName(className);
					unit.setCheckInterface(true);
				}
				else
				{
					String className = element.getName();
					unit.setClassName(className);
				}
				List<BodyDeclaration> members = element.getMembers();
				for (BodyDeclaration bd : members) 
				{
					if (bd instanceof FieldDeclaration ) 
					{
						if(((FieldDeclaration)bd).getType() instanceof PrimitiveType
								|| ((FieldDeclaration)bd).getType().toString().equals("String")
								|| ((FieldDeclaration)bd).getType().toString().equals("int[]"))
						{
							unit.getVariables().add((FieldDeclaration) bd);
						}
					} 
					else if (bd instanceof MethodDeclaration)
					{
						unit.getMethods().add((MethodDeclaration) bd);
					}
					else if(bd instanceof ConstructorDeclaration)
					{
						unit.getConstructors().add((ConstructorDeclaration) bd);
					}
				}
				collection.getUnits().add(unit);
			}
			
		}
		
		for(File file :files)
		{
			compileUnit = JavaParser.parse(file);
			List<TypeDeclaration> list = compileUnit.getTypes();
			Iterator<TypeDeclaration> itr = list.iterator();
			while (itr.hasNext())
			{
				
				TypeDeclaration element = itr.next();
				List<ClassOrInterfaceType> extendsList	=((ClassOrInterfaceDeclaration) element).getExtends();
				List<ClassOrInterfaceType> implementsList	=((ClassOrInterfaceDeclaration) element).getImplements();
			
				if(extendsList != null)
				{
					for (ClassOrInterfaceType extendedClassName : extendsList) 
					{
						Edge edge =  new Edge();
						String name = element.getName();
						Unit source = collection.getUnit(name);
						edge.setSource(source);
						String str = extendedClassName.getName();
						Unit destination = collection.getUnit(str);
						edge.setDestination(destination);
						edge.setType(EdgeTypes.EXTENDS);
						collection.getEdge().add(edge);
					}
				}
				if(implementsList!=null)
				{
					for (ClassOrInterfaceType implementClassName : implementsList) 
					{
						Edge edge =  new Edge();
						String name = element.getName();
						Unit source = collection.getUnit(name);
						edge.setSource(source);
						String str = implementClassName.getName();
						Unit destination = collection.getUnit(str);
						edge.setDestination(destination);
						edge.setType(EdgeTypes.IMPLEMENTS);
						collection.getEdge().add(edge);
					}
				}
				
			}
			
		}
		checkRelation(compilations, files,collection);
		return collection;
	}

	
	
	private void checkRelation(HashMap<String, CompilationUnit> compilations, List<File> files,Collection_Unit collection) throws ParseException, IOException
	{
		for (File file : files) 
		{
			CompilationUnit compileUnit = compilations.get(file.getName());
			List<TypeDeclaration> list = compileUnit.getTypes();
			for (TypeDeclaration element : list)
			{
				if(!((ClassOrInterfaceDeclaration)element).isInterface())
				{
				List<BodyDeclaration> members = element.getMembers();
				for (BodyDeclaration bd : members)
				{
					if (bd instanceof MethodDeclaration)
					{
						if(((MethodDeclaration) bd).getName().contains("main"))
						{
							Edge edgeForMain = new Edge();
							edgeForMain.setType(EdgeTypes.DEPENDENCY);
							String className = "Tester";
							Unit source  = collection.getUnit(className);
							String className1 = "Component";
							Unit destination  = collection.getUnit(className1);
							edgeForMain.setSource(source);
							edgeForMain.setDestination(destination);
							collection.getEdge().add(edgeForMain);
						}
						List<Parameter> parameters = ((MethodDeclaration) bd).getParameters();
						if(parameters!= null)
						{
							for (Parameter parameter : parameters) 
							{	
								if(parameter.getType()!=null)
								{
									if(checkReferenceType(parameter.getType()))
									{
										Edge edge = new Edge();
										edge.setType(EdgeTypes.DEPENDENCY);
										String className = element.getName();
										Unit source = collection.getUnit(className);
										edge.setSource(source);
										String str = parameter.getType().toString();
										Unit destination = collection.getUnit(str);
										edge.setDestination(destination);
										boolean status = collection.checkEdgePresent(source.getClassName(),destination.getClassName(),EdgeTypes.DEPENDENCY);
										if(status ==false)
										{
											collection.getEdge().add(edge);
										}
									}
								}
							}
						}
					}
					else if(bd instanceof FieldDeclaration)
					{
						boolean flag = checkReferenceType(((FieldDeclaration)bd).getType());
						if(flag)
						{
							
							Edge edge = new Edge();
							edge.setType(EdgeTypes.ASSOCIATION);
							String className = element.getName();
							Unit source = collection.getUnit(className);
							edge.setSource(source);
							String str = ((FieldDeclaration) bd).getType().toString();
							if(str.contains("Collection"))
							{
								edge.setCardinality("*");
								str=str.replace("Collection", "");
								str=str.replace("<", "");
								str=str.replace(">", "");
							}
							Unit destination = collection.getUnit(str);
							edge.setDestination(destination);
							boolean status = collection.checkEdgePresent(destination.getClassName(),source.getClassName(),EdgeTypes.ASSOCIATION);
							if(status ==false)
							{
								collection.getEdge().add(edge);
							}
						}
						
					}
					else if(bd instanceof ConstructorDeclaration)
					{

						List<Parameter> parameters = ((ConstructorDeclaration) bd).getParameters();
						if(parameters!= null)
						{
							for (Parameter parameter : parameters) 
							{	
								if(parameter.getType()!=null)
								{
									if(checkReferenceType(parameter.getType()))
									{
										Edge edge = new Edge();
										String str = parameter.getType().toString();
										Unit destination = collection.getUnit(str);
										if(destination.isCheckInterface())
										{
											edge.setType(EdgeTypes.DEPENDENCY);
											String className = element.getName();
											Unit source = collection.getUnit(className);
											edge.setSource(source);
											edge.setDestination(destination);
											boolean status = collection.checkEdgePresent(source.getClassName(),destination.getClassName(),EdgeTypes.DEPENDENCY);
											if(status ==false)
											{
												collection.getEdge().add(edge);
											
											}
										}
									}
								}
							}
						}
					
					}
				}
			}
			}
		}
	}



	private boolean checkReferenceType(Type type) 
	{
		if( type instanceof ReferenceType)
		{
			if( ! type.toString().equals("String"))
			{
				if( ! type.toString().equals("String[]") && (! type.toString().equals("int[]")))
				{
					return true;
				}
			}
		}
		return false;
	}
}


