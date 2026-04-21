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
import bending.DTO.DrinkTemperature;
import bending.DTO.SalesDto;
import bending.Service.AdminService;
import bending.tool.Const;

/**
 * Servlet implementation class Admin
 */
@WebServlet("/bending/admin")
public class Admin extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private AdminService adService = new AdminService();
	
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/jsp/admin.jsp");
		rd.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = request.getParameter(Const.PARAM_ACTION);
		HttpSession session = request.getSession();
		try {
			switch (action) {
			case "replenish":
				int drinkId = Integer.parseInt(request.getParameter("drinkId"));
			    int count = Integer.parseInt(request.getParameter("count"));
			    adService.replenish(session, drinkId, count);
				break;
			case "replace":
				int replaceId = Integer.parseInt(request.getParameter("drinkId"));
				DrinkDto newDrink = new DrinkDto();
				newDrink.setName(request.getParameter("name"));
				newDrink.setPrice(Integer.parseInt(request.getParameter("price")));
				newDrink.setTemperature(DrinkTemperature.valueOf(request.getParameter("temperature")));
				adService.replace(session, replaceId, newDrink);
				break;
			case "addCustom":
			    DrinkDto customDrink = new DrinkDto();
			    customDrink.setName(request.getParameter("name"));
			    customDrink.setPrice(Integer.parseInt(request.getParameter("price")));
			    customDrink.setTemperature(DrinkTemperature.valueOf(request.getParameter("temperature")));
			    adService.addCustomDrink(session, customDrink);
				break;
			case "collect":
				SalesDto sales = adService.collectSales(session);
				session.setAttribute("collectedSales", sales);
				break;
			}
		} catch (Exception e) {
			session.setAttribute("errorMsg", e.getMessage());
		}
		// PRGパターン、二重送信を防ぐ
		response.sendRedirect(request.getContextPath() + "/bending/admin");
	}

}
