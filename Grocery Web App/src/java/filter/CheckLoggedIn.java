package filter;

import java.io.IOException;
import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import managedbean.UserManager;

@WebFilter(filterName = "CheckLoggedIn", urlPatterns =
{
    "/*"
})
public class CheckLoggedIn implements Filter
{
    private FilterConfig filterConfig = null;
    
    @Inject
    UserManager um;

    public CheckLoggedIn()
    {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res,
            FilterChain chain)
            throws IOException, ServletException
    {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        HttpSession session = request.getSession(false);
        String loginURI = request.getContextPath() + "/faces/login.xhtml";
        String registerURI = request.getContextPath() + "/faces/register.xhtml";

        boolean loggedIn = um.credentialsAreOK();
        boolean loginRequest = request.getRequestURI().equals(loginURI);
        boolean registerRequest = request.getRequestURI().equals(registerURI);

        if (loggedIn || loginRequest || registerRequest)
        {
            chain.doFilter(request, response);
        }
        else
        {
            response.sendRedirect(loginURI);
        }
    }

    @Override
    public void destroy()
    {
        this.filterConfig = null;
    }

    @Override
    public void init(FilterConfig filterConfig)
    {
        this.filterConfig = filterConfig;
    }

    public void log(String msg)
    {
        filterConfig.getServletContext().log(msg);
    }

}
