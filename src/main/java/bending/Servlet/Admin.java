package bending.Servlet;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import bending.tool.Const;

/**
 * Servlet implementation class Admin
 */
@WebServlet("/bending/admin")
public class Admin extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher rd = request.getRequestDispatcher("/admin.jsp");
		rd.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = request.getParameter(Const.PARAM_ACTION);
		HttpSession session = request.getSession();
		try {
			dao.replenish(); //在庫補充
			dao.replace(); //商品入れ替え
			dao.addCustom(); //カスタム商品作成
			dao.collect(); //売上回収
	}

}
