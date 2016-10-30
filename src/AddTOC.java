import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddTOC {
	public static void main(String[] args) {
		File file;
		if(args.length > 0) {
			file = new File(args[0]);
			if(!file.exists()) {
				throw new RuntimeException("There is no such file");
			}
		} else {
			throw new RuntimeException("Please enter the file path");
		}
		
		InputStream in = null;
		Reader isr = null;
		BufferedReader br = null;
		PrintStream ps = null;
		try {
			in = new FileInputStream(file);
			isr = new InputStreamReader(in);
			br = new BufferedReader(isr);
			String line = null;
			StringBuilder sb = new StringBuilder();
			StringBuilder toc = new StringBuilder();
			while ((line = br.readLine()) != null) {
//				if(line.matches("(\\s*)\\*(\\s*)\\[(.+)\\]\\(#(.+)\\)")) {
//					listToc.add(line);
//				}
				
				if(line.matches("(\\s*)[#]+(\\s*).+")) {
//					line = line.trim();
					Pattern p = Pattern.compile("(\\s*)[#]+(\\s*)");
					Matcher m = p.matcher(line);
					String maodian = null;
					while(m.find()) {
						String title = line.substring(m.group().length());
						String dian = "*";
						for (int i = 0, len = m.group().trim().length(); i < len; i++) {
							if(i > 0) {
								dian = "	" + dian;
							}
						}
						dian += " ";
						maodian = URLEncoder.encode(title);
						toc.append(dian + "[" + title + "](#" + maodian + ")\n");
						break;
					}
					
					sb.append("<a name=\"" + maodian + "\"></a>\n\n");
				} 
				sb.append(line);
				sb.append("\n");
			}
			toc.append("\n\n");
			toc.append(sb.toString());
//			System.out.println(toc.toString());
			String tocFile = file.getParentFile() == null ? "TOC_" + file.getName() : file.getParentFile().getAbsolutePath() + File.separator + "TOC_" + file.getName();
			ps = new PrintStream(new FileOutputStream(tocFile));  
			ps.print(toc.toString());
			System.out.println("\n\nSuccess add TOC\n\nLook at the \"" + tocFile + "\" file\n\n");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			close(in, isr, br, ps);
		}
	}

	private static void close(InputStream in, Reader isr, BufferedReader br, PrintStream ps) {
		if (br != null) {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (isr != null) {
			try {
				isr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (in != null) {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (ps != null) {
			ps.close();
		}
	}
}
