package vis;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class CommonsFileUploadServlet
 */
public class CommonsFileUploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final String RESPONSE = "{\"success\": %s, \"%s\": \"%s\"}";
	private File tmpDir;
	private File destinationDir;
	private String[] fileExts;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CommonsFileUploadServlet() {
		super();
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		this.tmpDir = new File(this.getInitParameter("TMP_DIR_PATH"));
		if (!tmpDir.isDirectory()) {
			throw new ServletException(this.getInitParameter("TMP_DIR_PATH")
					+ " is not a directory");
		}
		String realPath = this.getServletContext().getRealPath(
				this.getInitParameter("DESTINATION_DIR_PATH"));
		this.destinationDir = new File(realPath);
		if (!this.destinationDir.isDirectory()) {
			throw new ServletException(
					this.getInitParameter("DESTINATION_DIR_PATH")
							+ " is not a directory");
		}
		this.fileExts = this.getInitParameter("FILE_EXT").split(",");
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.getWriter()
				.println("Invalid Method, please use POST instead.");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		try {
			String fileType = request.getHeader("X-File-Name").substring(
					request.getHeader("X-File-Name").lastIndexOf(".") + 1);
			List<String> list = Arrays.asList(this.fileExts);
			if (!list.contains(fileType)){
				out.printf(RESPONSE, "false", "details", "File type "
						+ fileType + " not allowed.");
				out.close();
				return;
			}

			File file = new File(this.destinationDir,
					request.getHeader("X-File-Name"));
			FileOutputStream fos = new FileOutputStream(file);
			int br;
			while ((br = request.getReader().read()) != -1) {
				fos.write(br);
			}
			fos.close();
			out.printf(this.RESPONSE, "true", "file",
					request.getContextPath()
							+ this.getInitParameter("DESTINATION_DIR_PATH")
									.concat("/") + file.getName());
			out.close();
		} catch (Exception ex) {
			out.printf(RESPONSE, "false", "details",
					"Error encountered while uploading file");
			out.close();
			log("Error encountered while uploading file", ex);
		}
	}

}
