/**************************************************
* * ף��ǽģ��
* * 2008-7-18
* **************************************************
* * msn:danxinju@hotmail.com
* * author:���¾�
***************************************************/
//��������
var wall = {
	backObj : null ,	//�洢ǰһ������
	backOrder : 0 ,	//�洢ǰһ����������
	objs : new Array() //��������
};

//����class���Է��ض���
wall.getElementsByClassName = function(className){
	var alls = document.getElementsByTagName("*");
	var rets = new Array();

	for (var i=0;i<alls.length ; i++)
	{
		if (alls[i].className == className)
		{
			rets.push(alls[i]);
		}
	}

	return rets;
};

//ʹС���ڿ��϶�
wall.makeDrag = function(){
	for (var i=0;i<this.objs.length ; i++)
	{
		Drag.init(this.objs[i]);
		this.objs[i].onDragStart = function(x ,y){
			wall.sort(this);
		};
		this.objs[i].getElementsByTagName("a")[0].onclick = function(){
			wall.close(this);
		};
	}
};

//�洢����
wall.saveObjs = function(){
	this.objs = this.getElementsByClassName("wall");
}

//С��������
wall.sort = function(obj){
	if (this.backObj)
	{
		this.backObj.style.zIndex = this.backOrder;
	}
	this.backObj = obj;
	this.backOrder = obj.style.zIndex;
	obj.style.zIndex = (this.objs.length + 1);
};

//�ر�С����
wall.close = function(obj){
	//�Ƴ�dom�ӵ�
	document.body.removeChild(obj.parentNode.parentNode);
}

//װ��wall����
wall.loadMethod = function(){
	this.saveObjs();	//�洢����
	this.makeDrag();	//ʹС�����϶�
};

//��ѯ��ʾ
wall.search = function(){
	var txt = document.getElementById("txt").value;
	if (txt == "")
	{
		return false;
	}
	var spans = this.getElementsByClassName("inline");
	for (var i=0;i<spans.length ;i++)
	{
		if (spans[i].innerHTML != txt)
		{
			spans[i].parentNode.parentNode.style.display = "none";
		}
		else
		{
			spans[i].parentNode.parentNode.style.display = "";
		}
	}
};

//����wall����
window.onload = function(){
	wall.loadMethod();
};