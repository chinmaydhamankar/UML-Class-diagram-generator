package com.parser.main;

import japa.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.parser.beans.Collection_Unit;
import com.parser.fileHandler.FileHandle;
import com.parser.parsercore.ParseCore;
import com.parser.parsercore.UMLDiagramGenerator;

public class Main {

	public static void main(String[] args) throws ParseException, IOException {
		// TODO Auto-generated method stub
		System.out.println("Length is---"+ args.length);


		System.out.println("Length is---"+ args.length);
		String folderName = args[0];
		

		System.out.println("Folder Name  is---"+ folderName);
		String outputName = args[1];
		

		System.out.println("Output Name  is---"+ outputName);
		
		
		
		
		File folder_Name = new File(folderName);
		
		
/*		File mainFolder = new File("../temp2");*/
		FileHandle fh = new FileHandle();
		ParseCore par = new ParseCore();
		UMLDiagramGenerator uml = new UMLDiagramGenerator();
		/*List<File> folders = fh.getAllFolders(mainFolder);
		for(File folder : folders)
		{
			List<File> files = fh.getAllFiles(folder);
			Collection_Unit cu = par.scanAndParseFile(files);
			uml.generateSyntax(cu,folder);
		}*/
	
		List<File> files = fh.getAllFiles(folder_Name);
		Collection_Unit cu = par.scanAndParseFile(files);
		uml.generateSyntax(cu,folder_Name,outputName);
	}

}
