package com.itheima.upload;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;

public class UploadServlet2 extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// request.setCharacterEncoding("UTF-8");
		// Ҫִ���ļ��ϴ��Ĳ���
		// �жϱ��Ƿ�֧���ļ��ϴ�������enctype="multipart/form-data"
		boolean isMultipartContent = ServletFileUpload
				.isMultipartContent(request);
		if (!isMultipartContent) {
			throw new RuntimeException("your form is not multipart/form-data");
		}

		// ����һ��DiskFileItemfactory������
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setRepository(new File("f:\\"));// ָ����ʱ�ļ��Ĵ洢Ŀ¼
		// ����һ��ServletFileUpload���Ķ���
		ServletFileUpload sfu = new ServletFileUpload(factory);
		// ����ϴ�������������
		sfu.setHeaderEncoding("UTF-8");
		// �����ϴ��ļ��Ĵ�С

		// ����request���󣬲��õ�һ������ļ���
		try {
			// sfu.setFileSizeMax(1024*1024*3);//��ʾ3M��С
			// sfu.setSizeMax(1024*1024*6);
			List<FileItem> fileItems = sfu.parseRequest(request);

			// ������������
			for (FileItem fileitem : fileItems) {
				if (fileitem.isFormField()) {
					// ��ͨ����
					processFormField(fileitem);
				} else {
					// �ϴ�����
					processUploadField(fileitem);
				}
			}

		} catch (FileUploadBase.FileSizeLimitExceededException e) {
			// throw new RuntimeException("�ļ����ڣ����ܳ���3M");

			System.out.println("�ļ����ڣ����ܳ���3M");
		} catch (FileUploadBase.SizeLimitExceededException e) {
			System.out.println("���ļ���С���ܳ���6M");
		} catch (FileUploadException e) {
			e.printStackTrace();
		}
	}

	private void processUploadField(FileItem fileitem) {
		try {
			// �õ��ļ�������
			InputStream is = fileitem.getInputStream();

			// ����һ���ļ����̵�Ŀ¼
			String directoryRealPath = this.getServletContext().getRealPath(
					"/WEB-INF/upload");
			File storeDirectory = new File(directoryRealPath);// �������ļ��ִ���Ŀ¼
			if (!storeDirectory.exists()) {
				storeDirectory.mkdirs();// ����һ��ָ����Ŀ¼
			}
			// �õ��ϴ�������
			String filename = fileitem.getName();// �ļ����е�ֵ F:\ͼƬ�ز�\С����\43.jpg ����
													// 43.jpg
			if (filename != null) {
				// filename =
				// filename.substring(filename.lastIndexOf(File.separator)+1);
				filename = FilenameUtils.getName(filename);// Ч��ͬ��
			}

			// ����ļ�ͬ��������
			filename = UUID.randomUUID() + "_" + filename;
			// Ŀ¼��ɢ
			// String childDirectory = makeChildDirectory(storeDirectory); //
			// 2015-10-19
			String childDirectory = makeChildDirectory(storeDirectory, filename); // a/b

			// �ϴ��ļ����Զ�ɾ����ʱ�ļ�
			File file = new File(storeDirectory, childDirectory
					+ File.separator + filename);
			System.out.println("uri == " + file.getAbsolutePath() + ",,fileName == " + file.getName());
			fileitem.write(file);
			fileitem.delete();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// �ϴ�����
	private void processUploadField1(FileItem fileitem) {

		try {
			// �õ��ļ�������
			InputStream is = fileitem.getInputStream();

			// ����һ���ļ����̵�Ŀ¼
			String directoryRealPath = this.getServletContext().getRealPath(
					"/WEB-INF/upload");
			File storeDirectory = new File(directoryRealPath);// �������ļ��ִ���Ŀ¼
			if (!storeDirectory.exists()) {
				storeDirectory.mkdirs();// ����һ��ָ����Ŀ¼
			}
			// �õ��ϴ�������
			String filename = fileitem.getName();// �ļ����е�ֵ F:\ͼƬ�ز�\С����\43.jpg ����
													// 43.jpg
			// �����ļ���
			/*
			 * ��������⣺ F:\\apache-tomcat-7.0.52\\webapps\\day18_00_upload\\upload\\F:\\ͼƬ�ز�\\С����\\41.jpg
			 */
			if (filename != null) {
				// filename =
				// filename.substring(filename.lastIndexOf(File.separator)+1);
				filename = FilenameUtils.getName(filename);// Ч��ͬ��
			}

			// ����ļ�ͬ��������
			filename = UUID.randomUUID() + "_" + filename;

			// Ŀ¼��ɢ
			// String childDirectory = makeChildDirectory(storeDirectory); //
			// 2015-10-19
			String childDirectory = makeChildDirectory(storeDirectory, filename); // 2015-10-19

			// ��storeDirectory�£���������Ŀ¼�µ��ļ�
			File file = new File(storeDirectory, childDirectory
					+ File.separator + filename); // ����Ŀ¼/����Ŀ¼/�ļ���
			// ͨ���ļ���������ϴ����ļ����浽����
			FileOutputStream fos = new FileOutputStream(file);

			int len = 0;
			byte[] b = new byte[1024];
			while ((len = is.read(b)) != -1) {
				fos.write(b, 0, len);
			}
			fos.close();
			is.close();

			fileitem.delete();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// Ŀ¼��ɢ
	private String makeChildDirectory(File storeDirectory, String filename) {
		int hashcode = filename.hashCode();// �����ַ�ת����32λhashcode��
		System.out.println(hashcode);
		String code = Integer.toHexString(hashcode); // ��hashcodeת��Ϊ16���Ƶ��ַ�
														// abdsaf2131safsd
		System.out.println(code);
		String childDirectory = code.charAt(0) + File.separator
				+ code.charAt(1); // a/b
		// ����ָ��Ŀ¼
		File file = new File(storeDirectory, childDirectory);
		if (!file.exists()) {
			file.mkdirs();
		}
		return childDirectory;
	}

	// �����ڴ�ɢ
	/*
	 * private String makeChildDirectory(File storeDirectory) {
	 * 
	 * SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd"); String
	 * dateDirectory = sdf.format(new Date()); //ֻ�ܴ���Ŀ¼ File file = new
	 * File(storeDirectory,dateDirectory); if(!file.exists()){ file.mkdirs(); }
	 * 
	 * return dateDirectory; }
	 */
	// ��ͨ����
	private void processFormField(FileItem fileitem) {
		try {
			String fieldname = fileitem.getFieldName();// �ֶ���
			String fieldvalue = fileitem.getString("UTF-8");// �ֶ�ֵ
			//fieldvalue = new String(fieldvalue.getBytes("iso-8859-1"),"utf-8");
			System.out.println(fieldname + "=" + fieldvalue);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
