var flag=1;
$('#rightArrow').click(function(){
	if(flag==0){
		$("#floatDivBoxs").animate({right: '-175px'},300);
		$(this).animate({right: '0px'},300);
		$(this).css('background-position','-50px 0');
		flag=1;
	}else{
		$("#floatDivBoxs").animate({right: '0'},300);
		$(this).animate({right: '170px'},300);
		$(this).css('background-position','0px 0');
		flag=0;
	}
});