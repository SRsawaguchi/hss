package jp.co.u_sha.hssystem;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class Yoyaku extends HttpServlet{
	static final String ALPHA_ID = "alpha";
	static final String ALPHA_PW = "alphapw";
	static final String BETA_ID = "beta";
	static final String BETA_PW = "betapw";

	private static final long serialVersionUID = -5940665776132028674L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("text/html; charset=Shift_JIS");
		PrintWriter out = resp.getWriter();
		
		String custId = req.getParameter("custId");
		String passWd = req.getParameter("passWd");
		
		if(auth(custId,passWd)){
			//認証に成功したらセッションを開始する。
			HttpSession session = req.getSession(true);
			session.setAttribute("userID", custId);
			printYoyakuHtml(out);
		}else{
			printFailHtml(out);
		}
	}
	
	private boolean auth(String custId,String passWd){
		boolean isSuccess = false;
		
		if(custId.equals(ALPHA_ID) && passWd.equals(ALPHA_PW)){
			isSuccess = true;
		}else if(custId.equals(BETA_ID) && passWd.equals(BETA_PW)){
			isSuccess = true;
		}
		
		return isSuccess;
	}
	
	private void printYoyakuHtml(PrintWriter out){
		out.println("<html>");
		out.println("<head>");
		out.println("<title>画面2　予約状況</title>");
		out.println("</head>");
		out.println("<body>");
		out.println("予約画面です。");
		out.println("<table border=\"1\">");
		printTableRow(out,"4/22(月)","10:00","10:30","11:00","11:30");
		printTableRow(out,"美容師A","×","×",makeYoyakuForm("4-22", "11:00", "biyo_a"),"×");
		printTableRow(out,"美容師B","×","×","×","×");
		printTableRow(out,"美容師C","×","×","×","×");
		out.println("</table>");
		out.println("</body>");
		out.println("</html>");
	}
	
	private void printFailHtml(PrintWriter out){
		out.println("<html>");
		out.println("<head>");
		out.println("<title>画面2　予約状況</title>");
		out.println("</head>");
		out.println("<body>");
		out.println("認証に失敗しました。");
		out.println("</body>");
		out.println("</html>");
	}
	
	private void printTableRow(PrintWriter out,String a,String b, String c ,String d,String e){
		out.println("<tr>");
		out.print("<th>" + a + "</th>");
		out.print("<th>" + b + "</th>");
		out.print("<th>" + c + "</th>");
		out.print("<th>" + d + "</th>");
		out.print("<th>" + e + "</th>");
		out.println("</tr>");
	}
	
	private void makeHiddenParam(StringBuilder sb,String name,String value){
		sb.append("<input type=\"hidden\" name=\"");
		sb.append(name);
		sb.append("\" value=\"");
		sb.append(value);
		sb.append("\"/>");
	}
	
	private String makeYoyakuForm(String date,String time,String biyoshi){
		StringBuilder sb = new StringBuilder();
		sb.append("<form action=\"wakuclick\" method=\"POST\">");
		makeHiddenParam(sb,"rsvDate", date);
		makeHiddenParam(sb,"rsvTime", time);
		makeHiddenParam(sb,"Biyoshi", biyoshi);
		sb.append("<input type=\"submit\" value=\"○\"/>");
		sb.append("</form>");
		return sb.toString();
	}
}
