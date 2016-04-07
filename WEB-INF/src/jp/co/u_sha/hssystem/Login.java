package jp.co.u_sha.hssystem;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Login extends HttpServlet{
	private static final long serialVersionUID = 2675461043591790084L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("text/html; charset=Shift_JIS");
		PrintWriter out = resp.getWriter();
		
		out.println("<html>");
		out.println("<head>");
		out.println("<title>画面１　ログイン</title>");
		out.println("</head>");
		out.println("<body>");
		out.println("<form action=\"http://localhost/hss/yoyaku\" method=\"POST\">");
		out.println("<p><label>顧客ID      <input type=\"text\" name=\"custId\"/></label></p>");
		out.println("<p><label>パスワード            <input type=\"text\" name=\"passWd\"/></label></p>");
		out.println("<p><input type=\"submit\" value=\"ログイン\"/></p>");
		out.println("</form>");
		out.println("</body>");
		out.println("</html>");
	}
}
