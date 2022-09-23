import user from './user'
import cases from "@/api/project/cases";
import task from "@/api/project/task";
import jenkins from "@/api/project/jenkins";
import report from "@/api/project/report";
import project from "@/api/project/project";
import file from "@/api/project/file";

const api = {
    user,
    cases,
    task,
    jenkins,
    report,
    project,
    file
}
export default api
