package site.keyu.minigit.servlet;

import org.eclipse.jgit.http.server.GitServlet;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

@WebServlet(name = "gitRepoServlet",urlPatterns = {"/repo/*"},
            loadOnStartup = 1,
            initParams = {
                @WebInitParam(name="base-path",value = "C:/MiniGit"),
                @WebInitParam(name="export-all",value = "true")
            })
public class GitRepoServlet extends GitServlet {

}
