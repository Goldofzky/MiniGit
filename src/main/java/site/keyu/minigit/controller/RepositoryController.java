package site.keyu.minigit.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import site.keyu.minigit.config.GitEnv;
import site.keyu.minigit.service.RepositoryService;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * 仓库基本信息
 */
@Controller
public class RepositoryController {

    @Autowired
    private GitEnv gitEnv;

    @Autowired
    private RepositoryService repositoryService;


    private Logger logger = LoggerFactory.getLogger(RepositoryController.class);


    /**
     * 提供repo的压缩包下载
     *
     * @param group
     * @param repo
     * @return
     */
    @GetMapping(path = "/api/{group}/{repo}/download")
    public StreamingResponseBody download(@PathVariable("group") String group,
                                          @PathVariable("repo") String repo,
                                          HttpServletResponse response) {

        try {
            String downloadPath = this.repositoryService.zipRepo(group, repo);
            response.setHeader("Content-Disposition", "attachment;filename=" + repo + ".zip");
            final InputStream is = new FileInputStream(downloadPath);
            return outputStream -> {
                int nRead;
                byte[] data = new byte[1024];
                while ((nRead = is.read(data, 0, data.length)) != -1) {
                    outputStream.write(data, 0, nRead);
                }
                outputStream.flush();
                outputStream.close();
                //删除缓冲区的压缩文件
                //TODO

            };
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return null;
    }


//    @GetMapping(path = "/*")
//    public Object get() throws Exception{
//
//        return new FileRepository("C:\\MiniGit\\User1\\MyGit\\.git");
//    }

}
