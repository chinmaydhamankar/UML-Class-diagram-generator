package com.parser.fileHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileHandle {

	public List<File> getAllFolders(File mainFolder)
	{
		List<File> folders = new ArrayList<File>();
		File[] getFolders = mainFolder.listFiles();
		for(File folder : getFolders)
		{
			if(folder.isDirectory())
			{
				folders.add(folder);
			}
		}
		return folders;
	}
	
	public List<File> getAllFiles(File folder)
	{
		List<File> files = new ArrayList<File>();
		File[] getfiles = folder.listFiles();
		for(File file : getfiles)
		{
			if(file.isFile() && file.getName().endsWith("java"))
			{
				files.add(file);
			}
		}
		return files;
	}
	
}
