var links =document.links;
for(var i=0,k=links.length;i<k;i++){
	links[i].setAttribute("target","_self");
}
try{
	process.mainModule.exports.closeLoading();
}finally{

}
