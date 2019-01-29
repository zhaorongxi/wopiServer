# wopiServer
springboot集成officeOnline在线预览

项目无需任何其他组件支持,直接启动即可. 部署在安装好了的officeOnLine服务器上
http://{域名或者ip端口}/wv/wordviewerframe.aspx?WOPISrc=http://{域名或者ip端口}/wopi/files/{name}&access_token=#{token}
第一个域名与ip端口为officeOnLine服务器的ip端口,第二个为你的服务部署的域名或者ip端口,服务启动后通过这个固定请求路径访问即可
