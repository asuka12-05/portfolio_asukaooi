package bending.Servlet;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import bending.DTO.DrinkDto;
import bending.Service.ConsumerService;
import bending.tool.Const;

/**
 * Servlet implementation class Start
 */
@WebServlet("/bending/consumer")
public class Consumer extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ConsumerService conService = new ConsumerService();

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
	    conService.getDrinkList(session);
		RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/jsp/consumer.jsp");
		rd.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = request.getParameter(Const.PARAM_ACTION);
		HttpSession session = request.getSession();
		try {
			// 商品を購入しおつりを返金
			if ("buy".equals(action)) {
				int drinkId = Integer.parseInt(request.getParameter("drinkId"));
				int change = conService.purchase(session, drinkId);
				session.setAttribute("change", change);
				DrinkDto gotDrink = conService.findById(session, drinkId);
			    session.setAttribute("gotDrink", gotDrink);
			// 商品を購入せず投入金額をそのまま返金
			} else if ("return".equals(action)) {
				int change = conService.returnChange(session);
				session.setAttribute("change", change);
			// 投入額をサーバー側で管理
			} else if ("insertMoney".equals(action)) {
				int amount = Integer.parseInt(request.getParameter("amount"));
				conService.insertMoney(session, amount);
			}
		} catch (Exception e) {
			session.setAttribute("errorMsg", e.getMessage());
		}
		// PRGパターン、二重送信を防ぐ
		response.sendRedirect(request.getContextPath() + "/bending/consumer");
	}

}
