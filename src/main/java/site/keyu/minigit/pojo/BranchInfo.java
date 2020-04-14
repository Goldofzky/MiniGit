package site.keyu.minigit.pojo;

import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;

/**
 * 分支信息
 */
public class BranchInfo {
    //分支顶部commit
    private RevCommit headCommit;

    //分支Ref
    private Ref ref;

    public RevCommit getHeadCommit() {
        return headCommit;
    }

    public void setHeadCommit(RevCommit headCommit) {
        this.headCommit = headCommit;
    }

    public Ref getRef() {
        return ref;
    }

    public void setRef(Ref ref) {
        this.ref = ref;
    }
}
