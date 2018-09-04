package com.wondertek.mam.util.others;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;

public class ShellUtil implements Serializable {
	private static final long serialVersionUID = -5235273384829550242L;
	static String NEWLINE = "\r\n";
	private static final String CHARSET_ENCODE = "UTF-8";

	public static boolean execCmd(String[] comm) {
		return execCmd(comm, null);
	}

	public static boolean execCmd(String comm, StringBuffer message) {
		return execCmd(new String[] { comm }, message);
	}

	public static boolean execCmd(String[] comm, StringBuffer message) {
		if (comm == null || comm.length == 0)
			return false;

		boolean result = true;
		Process proc = null;
		BufferedReader buf = null;
		InputStream in = null;
		try {

			if (comm.length == 1)
				proc = Runtime.getRuntime().exec(comm[0]);
			else
				proc = Runtime.getRuntime().exec(comm);
			in = proc.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(in, CHARSET_ENCODE));
			String line = null;

			while (null != (line = br.readLine())) {

			}
			try {
				// 等待执行完再往后执行
				proc.waitFor();
				buf = new BufferedReader(new InputStreamReader(proc.getErrorStream(), CHARSET_ENCODE));
				while ((line = buf.readLine()) != null) {
					if (message != null) {
						message.append(line).append("<br>");
					}
				}
			} catch (InterruptedException e) {
				result = false;
				e.printStackTrace();
			}
		} catch (IOException e) {
			result = false;
			e.printStackTrace();
		} finally {
			if (null != buf)
				try {
					buf.close();
				} catch (IOException e) {
				}

			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
			if (null != proc)
				proc.destroy();
		}
		return result;
	}

}
