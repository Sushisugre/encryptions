package encryptor.file;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class S4Filter extends FileFilter {
	
	private String extension;
    
	public S4Filter(String extension)
	{
		this.extension=extension;
	}
	
	@Override
	public boolean accept(File f)
	{
		if(f.isDirectory())
		{
			return true;
		}
		
		String extension=getExtension(f);
		if(extension.toLowerCase().equals(this.extension.toLowerCase()))
		{
			return true;
		}
		return false;
	}

	@Override
	public String getDescription() {
		
		return this.extension.toUpperCase();
	}
	
	private String getExtension(File file)
	{
		String name = file.getName();
		int index=name.lastIndexOf('.');
		
		if(index==-1)
		{
			return "";
		}
		else
		{
		return name.substring(index+1).toLowerCase();	
		}
	}

}
