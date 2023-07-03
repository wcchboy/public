package com.igrs.sml.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class Temp {
	public static final String[] Surname = { "赵", "钱", "孙", "李", "周", "吴", "郑",
			"王", "冯", "陈", "褚", "卫", "蒋", "沈", "韩", "杨", "朱", "秦", "尤", "许",
			"何", "吕", "施", "张", "孔", "曹", "严", "华", "金", "魏", "陶", "姜", "戚",
			"谢", "邹", "喻", "柏", "水", "窦", "章", "云", "苏", "潘", "葛", "奚", "范",
			"彭", "郎", "鲁", "韦", "昌", "马", "苗", "凤", "花", "方", "俞", "任", "袁",
			"柳", "酆", "鲍", "史", "唐", "费", "廉", "岑", "薛", "雷", "贺", "倪", "汤",
			"滕", "殷", "罗", "毕", "郝", "邬", "安", "常", "乐", "于", "时", "傅", "皮",
			"卞", "齐", "康", "伍", "余", "元", "卜", "顾", "孟", "平", "黄", "和", "穆",
			"萧", "尹", "姚", "邵", "湛", "汪", "祁", "毛", "禹", "狄", "米", "贝", "明",
			"臧", "计", "伏", "成", "戴", "谈", "宋", "茅", "庞", "熊", "纪", "舒", "屈",
			"项", "祝", "董", "梁", "杜", "阮", "蓝", "闵", "席", "季", "麻", "强", "贾",
			"路", "娄", "危", "江", "童", "颜", "郭", "梅", "盛", "林", "刁", "钟", "徐",
			"邱", "骆", "高", "夏", "蔡", "田", "樊", "胡", "凌", "霍", "虞", "万", "支",
			"柯", "昝", "管", "卢", "莫", "经", "房", "裘", "缪", "干", "解", "应", "宗",
			"丁", "宣", "贲", "邓", "郁", "单", "杭", "洪", "包", "诸", "左", "石", "崔",
			"吉", "钮", "龚", "程", "嵇", "邢", "滑", "裴", "陆", "荣", "翁", "荀", "羊",
			"于", "惠", "甄", "曲", "家", "封", "芮", "羿", "储", "靳", "汲", "邴", "糜",
			"松", "井", "段", "富", "巫", "乌", "焦", "巴", "弓", "牧", "隗", "山", "谷",
			"车", "侯", "宓", "蓬", "全", "郗", "班", "仰", "秋", "仲", "伊", "宫", "宁",
			"仇", "栾", "暴", "甘", "钭", "厉", "戎", "祖", "武", "符", "刘", "景", "詹",
			"束", "龙", "叶", "幸", "司", "韶", "郜", "黎", "蓟", "溥", "印", "宿", "白",
			"怀", "蒲", "邰", "从", "鄂", "索", "咸", "籍", "赖", "卓", "蔺", "屠", "蒙",
			"池", "乔", "阴", "郁", "胥", "能", "苍", "双", "闻", "莘", "党", "翟", "谭",
			"贡", "劳", "逄", "姬", "申", "扶", "堵", "冉", "宰", "郦", "雍", "却", "璩",
			"桑", "桂", "濮", "牛", "寿", "通", "边", "扈", "燕", "冀", "浦", "尚", "农",
			"温", "别", "庄", "晏", "柴", "瞿", "阎", "充", "慕", "连", "茹", "习", "宦",
			"艾", "鱼", "容", "向", "古", "易", "慎", "戈", "廖", "庾", "终", "暨", "居",
			"衡", "步", "都", "耿", "满", "弘", "匡", "国", "文", "寇", "广", "禄", "阙",
			"东", "欧", "殳", "沃", "利", "蔚", "越", "夔", "隆", "师", "巩", "厍", "聂",
			"晁", "勾", "敖", "融", "冷", "訾", "辛", "阚", "那", "简", "饶", "空", "曾",
			"毋", "沙", "乜", "养", "鞠", "须", "丰", "巢", "关", "蒯", "相", "查", "后",
			"荆", "红", "游", "郏", "竺", "权", "逯", "盖", "益", "桓", "公", "仉", "督",
			"岳", "帅", "缑", "亢", "况", "郈", "有", "琴", "归", "海", "晋", "楚", "闫",
			"法", "汝", "鄢", "涂", "钦", "商", "牟", "佘", "佴", "伯", "赏", "墨", "哈",
			"谯", "篁", "年", "爱", "阳", "佟", "言", "福", "南", "火", "铁", "迟", "漆",
			"官", "冼", "真", "展", "繁", "檀", "祭", "密", "敬", "揭", "舜", "楼", "疏",
			"冒", "浑", "挚", "胶", "随", "高", "皋", "原", "种", "练", "弥", "仓", "眭",
			"蹇", "覃", "阿", "门", "恽", "来", "綦", "召", "仪", "风", "介", "巨", "木",
			"京", "狐", "郇", "虎", "枚", "抗", "达", "杞", "苌", "折", "麦", "庆", "过",
			"竹", "端", "鲜", "皇", "亓", "老", "是", "秘", "畅", "邝", "还", "宾", "闾",
			"辜", "纵", "侴", "万俟", "司马", "上官", "欧阳", "夏侯", "诸葛", "闻人", "东方",
			"赫连", "皇甫", "羊舌", "尉迟", "公羊", "澹台", "公冶", "宗正", "濮阳", "淳于", "单于",
			"太叔", "申屠", "公孙", "仲孙", "轩辕", "令狐", "钟离", "宇文", "长孙", "慕容", "鲜于",
			"闾丘", "司徒", "司空", "兀官", "司寇", "南门", "呼延", "子车", "颛孙", "端木", "巫马",
			"公西", "漆雕", "车正", "壤驷", "公良", "拓跋", "夹谷", "宰父", "谷梁", "段干", "百里",
			"东郭", "微生", "梁丘", "左丘", "东门", "西门", "南宫", "第五", "公仪", "公乘", "太史",
			"仲长", "叔孙", "屈突", "尔朱", "东乡", "相里", "胡母", "司城", "张廖", "雍门", "毋丘",
			"贺兰", "綦毋", "屋庐", "独孤", "南郭", "北宫", "王孙" };
	
	 public static String randomName() {  
	        Random random=new Random();  
	        int index=random.nextInt(Surname.length-1);       
	        String name = Surname[index]; //获得一个随机的姓氏  
	          
	        /* 从常用字中选取一个或两个字作为名 */  
	        if(random.nextBoolean()){  
	            name+=getChinese()+getChinese();  
	        }else {  
	            name+=getChinese();  
	        }  
	        return name;
	    }  
	public static String getChinese() {  
        String str = null;  
        int highPos, lowPos;  
        Random random = new Random();  
        highPos = (176 + Math.abs(random.nextInt(71)));//区码，0xA0打头，从第16区开始，即0xB0=11*16=176,16~55一级汉字，56~87二级汉字  
        random=new Random();  
        lowPos = 161 + Math.abs(random.nextInt(94));//位码，0xA0打头，范围第1~94列  
  
        byte[] bArr = new byte[2];  
        bArr[0] = (new Integer(highPos)).byteValue();  
        bArr[1] = (new Integer(lowPos)).byteValue();  
        try {  
            str = new String(bArr, "GB2312");   //区位码组合成汉字  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
            return str;  
    } 
	public static String getId_card() {
		String[] array = { "211204197202222591", "620403198305277872",
				"542124197206145754", "51110019700622539X",
				"542126198107125894", "140930198702123393",
				"140522197303116915", "32058319850416587X",
				"370321197307169876", "371623197607208558",
				"451281199001106435", "330702197308176897",
				"411723198103147571", "371100197402156211",
				"612523198007119471", "421221198809243030",
				"130421198106267539", "420116198601283503",
				"610200197004159489", "321201199001219682",
				"320504198303279007", "542333199008252045",
				"220303198404232063", "520325197301114982",
				"62030219800527626X", "231001198009242281",
				"231024198504246743", "650204197503118684",
				"430424198104288889", "141030198903197681",
				"360101198609162748", "542421197808152123",
				"340824198109123960", "441322197206183244",
				"41132719740920278X", "150303197501257286",
				"500240198202202580" };
		int index = new Random().nextInt(array.length - 1);
		return array[index];
	}

	public static String getTitle() {

		String[] array = { "我不想放弃，但真的很累。",
		"回头看看当初也全是真心",
		"别拿我的秘密换取你和别人的交情",
		"爱情从来都是友情滋生的",
		"听说过我就行没必要认识我",
		"总是高估了在别人心中的位置，然后笑了笑自己",
		"你说你和我在一起很累，可我把最好的给了你",
		"如果不是心软那我会很强大",
		"你以为可以一直陪你的人却没有伴你走到最后",
		"那个让你刻骨铭心的名字，始终不是我的",
		"敬我干净如始，敬你漂泊无依，我干杯你随意",
		"我只希望你开心，仅此而已",
		"遇见你像是冬日暖阳",
		"你是想环游世界，还是逃避孤独",
		"每天明明都无聊至极，却每次都熬到深夜",
		"如果孤独是自由的副作用，那我爱极了，至少一个人快活",
		"吹冷风喝烈酒 ，没有什么永垂不朽",
		"讨厌孤独，又上了孤独的瘾。 讨厌情感，又中了爱你的毒",
		"一个人，一座城，孤独终老",
		"在我追不上的距离， 担心你一个人孤寂",
		"孤单不是与生俱来，而是由你爱上一个人的那一刻开始",
		"说一句我爱你吧，假的也好",
		"你当然不心疼，反正难过的是我",
		"舍不得又怎样，到最后有些人还不是说散就散",
		"薄情总是被记住，深情却总被辜负",
		"我等的船还不来，我等的人还不明白",
		"你说别在笑了，眼泪都快掉了",
		"你敷衍这么明显我怎么可能看不见",
		"我用一个人的执着，对待两个人的寂寞",
		"暖黄色的路灯下，一人走过，陪伴的只有-----影子",
		"最深的孤独，是你明知道自己的渴望，却得对它装聋作哑",
		"透过黑暗，我看见孤独的心",
		"我莫名又来了孤独感 可城市分明人山人海",
		"偶尔心动 惯性拒绝 习惯孤独 我，丧得一败涂地",
		"每个人都有一个世界安静且孤独",
		"我听的是歌，也是自己内心无人能懂的诉说",
		"收起你那泛滥的爱，你给过别人的，我不稀罕",
		"不要等我失望了才知道珍惜",
		"同样都是人，怎么有的心机那么深",
		"泛滥就泛滥，我认真的时候输得太惨",
		"从来未顺利遇上好景降临，如何能重拾信心",
		"动了真心，怎能说忘就忘",
		"因为得不到，所以假装不想要",
		"别跟我谈感情，想想就恶心",
		"原谅我辜负了你一世情深",
		"感情这种事你放不下你活该",
		"听着你爱听的歌，笑了好久，却又伤心了好久"
		};
		int index = new Random().nextInt(array.length - 1);
		return array[index];
	}

	public static String getCertificate() {
		return randomStr(15);
	}

	public static String randomStr(int length) {
		String[] strArray = { "2", "3", "4", "5", "6", "7", "8", "9", "a", "b",
				"c", "d", "e", "f", "g", "h", "j", "k", "m", "n", "p", "q",
				"r", "s", "t", "u", "v", "w", "x", "y", "z" };
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			Random r = new Random();
			sb.append(strArray[r.nextInt(26)]);
		}
		return sb.toString();
	}
	private static int getNum(int inNum) {
		inNum--;
		System.out.println(inNum);
		if (inNum > 0) {
			return getNum(inNum);
		}
		return inNum;
	}

	/**
	 * 获取随机日期
	 * 
	 * @param beginDate
	 *            起始日期，格式为：yyyy-MM-dd
	 * @param endDate
	 *            结束日期，格式为：yyyy-MM-dd
	 * @return
	 */

	public static Timestamp randomDate(String beginDate, String endDate) {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date start = format.parse(beginDate);// 构造开始日期
			Date end = format.parse(endDate);// 构造结束日期
			// getTime()表示返回自 1970 年 1 月 1 日 00:00:00 GMT 以来此 Date 对象表示的毫秒数。
			if (start.getTime() >= end.getTime()) {
				return null;
			}
			long date = random(start.getTime(), end.getTime());

			return new Timestamp(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static long random(long begin, long end) {
		long rtn = begin + (long) (Math.random() * (end - begin));
		// 如果返回的是开始时间和结束时间，则递归调用本函数查找随机值
		if (rtn == begin || rtn == end) {
			return random(begin, end);
		}
		return rtn;
	}

	public static void main(String[] args) {
		// System.out.println(Temp.getSpecialty("", 4));
		for (int i = 0; i < 10; i++) {
			// System.out.println(Temp.getSpecialty());
			Date randomDate = randomDate("2010-01-01", "2017-04-20");
			System.out.println(randomDate.toString());
		}

	}
	
	public static String getPic(){
		int simg= new Random().nextInt(Temp.IMAGES.length - 1);
		return Temp.IMAGES[simg];
	}
	public static final String[] IMAGES = new String[] {
		"http://ww3.sinaimg.cn/mw690/49282834jw1f46a6rsqwhj21w01w0e84.jpg",
		"http://ww2.sinaimg.cn/mw690/49282834gw1f5qqybicd4j218g0tntlk.jpg",
		"http://ww3.sinaimg.cn/mw690/49282834jw1f4y56euyouj21w019dqkf.jpg",
		"http://ww1.sinaimg.cn/mw1024/49282834jw1ezjrfaobyjj21uf2w9nik.jpg",
		"http://ww2.sinaimg.cn/mw690/005RQmSDgw1f6liwh7dbmj30qo0zk1kx.jpg",
		"http://ww1.sinaimg.cn/mw690/005RQmSDgw1f6liwd6kgkj30qo0zk1gx.jpg",
		"http://ww1.sinaimg.cn/mw690/005RQmSDjw1f5zkbs7ywqj30kr0urk5v.jpg",
		"http://ww1.sinaimg.cn/mw690/005RQmSDjw1f5zkaipvjtj31hc1z4e81.jpg",
		"http://ww2.sinaimg.cn/mw690/005RQmSDjw1f5zkatlvglj31hc1z41ky.jpg",
		"http://ww2.sinaimg.cn/mw690/005RQmSDjw1f5zkb668jgj31hc1z41ky.jpg",
		"http://ww4.sinaimg.cn/mw690/005RQmSDjw1f5zkbfefg8j31hc1z47wi.jpg",
		"http://ww4.sinaimg.cn/mw690/005RQmSDjw1f5zkbnqx3tj31hc1z4npd.jpg",
		"http://ww2.sinaimg.cn/mw690/005RQmSDjw1f5jab4yvrbj30gi0gidru.jpg",
		"http://ww1.sinaimg.cn/mw690/005RQmSDgw1f5hrbzg4i4j30qo0zk4qp.jpg",
		"http://ww3.sinaimg.cn/mw690/005RQmSDgw1f5enxu6fprj30qo0zk7wh.jpg",
		"http://ww2.sinaimg.cn/mw690/005RQmSDgw1f4z9vou2rlj31w019cnef.jpg",
		"http://ww1.sinaimg.cn/mw690/005RQmSDgw1f4z9vnkhcgj30yf170qkj.jpg",
		"http://ww4.sinaimg.cn/mw690/005RQmSDgw1f4tnmn2q5tj319c1w0e83.jpg",
		"http://ww4.sinaimg.cn/mw690/005RQmSDgw1f4tnmsepgcj319c1w0hdv.jpg",
		"http://ww3.sinaimg.cn/mw690/005RQmSDgw1f4tnmxcf68j319c1w0qv7.jpg",
		"http://ww4.sinaimg.cn/mw690/005RQmSDgw1f4tnn39kyij319c1w0qv7.jpg",
		"http://ww4.sinaimg.cn/mw690/005RQmSDgw1f4tnn7z72qj319c1w0npf.jpg",
		"http://ww3.sinaimg.cn/mw690/005RQmSDgw1f4ssp4b1bwj30qo0zhagg.jpg",
		"http://ww2.sinaimg.cn/mw690/005RQmSDgw1f4ssp56etzj30qo0zh7an.jpg",
		"http://ww4.sinaimg.cn/mw690/005RQmSDgw1f4ssp3cg79j30qo0zhwm0.jpg",
		"http://ww3.sinaimg.cn/mw690/005RQmSDgw1f4ssp5mse7j30qo0zhtfm.jpg",
		"http://ww1.sinaimg.cn/mw690/005RQmSDgw1f4ssqk3svcj30qo0zh0yo.jpg",
		"http://ww1.sinaimg.cn/mw690/005RQmSDgw1f4ssqkwy1hj30qo0zh117.jpg",
		"http://ww1.sinaimg.cn/mw690/005RQmSDgw1f4sifvxiffj30qo0wj128.jpg",
		"http://ww3.sinaimg.cn/mw690/005RQmSDgw1f4sifv2tr8j30qo0zkgro.jpg",
		"http://ww2.sinaimg.cn/mw690/005RQmSDgw1f4sa4ljsulj30qo0zktfb.jpg",
		"http://ww1.sinaimg.cn/mw690/005RQmSDgw1f4q0dlmok7j30oy0zpdom.jpg",
		"http://ww4.sinaimg.cn/mw690/005RQmSDgw1f4q0ebsrvuj30r40xrajc.jpg",
		"http://ww2.sinaimg.cn/mw690/005RQmSDgw1f4q0ef3zarj30u3197tm4.jpg",
		"http://ww1.sinaimg.cn/mw690/005RQmSDgw1f4q0egevavj30uq13tdsh.jpg",
		"http://ww2.sinaimg.cn/mw690/005RQmSDgw1f4q0eadanyj31w019ce83.jpg",
		"http://ww3.sinaimg.cn/mw690/005RQmSDgw1f4q0elthxaj31w019c7wj.jpg",
		"http://ww2.sinaimg.cn/mw690/005RQmSDgw1f4q0et84orj319c1w0hdv.jpg",
		"http://ww2.sinaimg.cn/mw690/005RQmSDgw1f4q0djabdqj319c1w04qs.jpg",
		"http://ww1.sinaimg.cn/mw690/005RQmSDgw1f4p7v80novj30zk0qoqa2.jpg",
		"http://ww3.sinaimg.cn/mw690/005RQmSDgw1f4ou67564zj30qo0zkn4w.jpg",
		"http://ww4.sinaimg.cn/mw690/005RQmSDgw1f4o0ejs644j30qo0zkqrj.jpg",
		"http://ww2.sinaimg.cn/mw690/005RQmSDgw1f4o0ehohzej30qo0zk4my.jpg",
		"http://ww1.sinaimg.cn/mw690/005RQmSDgw1f4o0en6oc9j30qo0zkqtp.jpg",
		"http://ww4.sinaimg.cn/mw690/005RQmSDgw1f4o0ep7vqdj30qo0zkqqy.jpg",
		"http://ww3.sinaimg.cn/mw690/005RQmSDjw1f4hbf5u9f5j30qo140n2y.jpg",
		"http://ww2.sinaimg.cn/mw690/005RQmSDjw1f4hbf527j2j30qo0zktdx.jpg",
		"http://ww1.sinaimg.cn/mw690/005RQmSDgw1f4e7d54kxwj31w019c4qr.jpg",
		"http://ww3.sinaimg.cn/mw690/005RQmSDgw1f4e78fj7bvj319c1w0hdv.jpg",
		"http://ww4.sinaimg.cn/mw690/005RQmSDgw1f4e7exj5bij319c19ckjm.jpg",
		"http://ww4.sinaimg.cn/mw690/005RQmSDgw1f49s21i95bj30qo0zk45d.jpg",
		"http://ww3.sinaimg.cn/mw690/005RQmSDjw1f3vytfg7a0j30ku0rsttj.jpg",
		"http://ww1.sinaimg.cn/mw690/005RQmSDjw1f3ttn6r2d6j319c1w07wk.jpg",
		"http://ww4.sinaimg.cn/mw690/005RQmSDgw1f3dw5v0i2aj30u0140k39.jpg",
		"http://ww3.sinaimg.cn/mw690/005RQmSDjw1f3b2xtw2lyj30qo0zk1f7.jpg",
		"http://ww4.sinaimg.cn/mw690/005RQmSDgw1f36ttmoi6jj30qo0zk1kx.jpg",
		"http://ww3.sinaimg.cn/mw690/005RQmSDgw1f1v7cfhc8bj30ku0rswjb.jpg",
		"http://ww4.sinaimg.cn/mw690/005RQmSDjw1f1ufoi7g3vj30qo0zk0z8.jpg",
		"http://ww2.sinaimg.cn/mw690/005RQmSDjw1f1uforlutzj30qo0zkjwg.jpg",
		"http://ww4.sinaimg.cn/mw690/005RQmSDgw1f1rn3qo9osj30qo123n7q.jpg",
		"http://ww3.sinaimg.cn/mw690/005RQmSDgw1f1juwyvn6tj30ku0owncg.jpg",
		"http://ww2.sinaimg.cn/mw690/005RQmSDjw1f0s55b9hohj30qo0zkwno.jpg",
		"http://f.hiphotos.bdimg.com/imgad/pic/item/77c6a7efce1b9d1608043b8cf6deb48f8d546418.jpg",
		"http://imgsrc.baidu.com/forum/pic/item/993b5ee124e2125418d81fe7.jpg",
		"http://upload.trends.com.cn/2014/0513/1399946011133.jpg",
		"http://img.pconline.com.cn/images/upload/upc/tx/photoblog/1503/17/c2/3974346_1426551981202_mthumb.jpg",
		"http://img2.3lian.com/2014/f2/159/40.jpg",
		"http://imgsrc.baidu.com/forum/pic/item/52b06d380cd79123a1ae34f2ad345982b3b7809e.jpg",
		"http://c.hiphotos.baidu.com/image/pic/item/f636afc379310a55515bfd76b54543a982261030.jpg",
		"http://f.hiphotos.baidu.com/image/pic/item/2e2eb9389b504fc2b8359387e7dde71191ef6dcd.jpg",
		"http://c.hiphotos.baidu.com/image/pic/item/8435e5dde71190ef0491b15dcc1b9d16fcfa60cd.jpg",
		"http://f.hiphotos.baidu.com/image/pic/item/91ef76c6a7efce1be841eb48ad51f3deb58f65cd.jpg",
		"http://h.hiphotos.baidu.com/image/pic/item/b8014a90f603738d468c2ca0b11bb051f919ec6a.jpg",
		"http://e.hiphotos.baidu.com/image/pic/item/2f738bd4b31c87015d709264257f9e2f0608ff6a.jpg",
		"http://d.hiphotos.baidu.com/image/pic/item/b2de9c82d158ccbf5fcdf4b21bd8bc3eb03541c0.jpg",
		"http://c.hiphotos.baidu.com/image/pic/item/810a19d8bc3eb135298010bba41ea8d3fd1f446e.jpg",
		"http://h.hiphotos.baidu.com/image/pic/item/a8ec8a13632762d0600802bfa2ec08fa513dc6f8.jpg",
		"http://e.hiphotos.baidu.com/image/pic/item/30adcbef76094b3620fac9f5a1cc7cd98d109dba.jpg",
		"http://g.hiphotos.baidu.com/image/pic/item/21a4462309f79052061bef3e0ef3d7ca7bcbd5ae.jpg",
		"http://a.hiphotos.baidu.com/image/pic/item/7c1ed21b0ef41bd5ed144ef453da81cb39db3d6e.jpg",
		"http://g.hiphotos.baidu.com/image/pic/item/80cb39dbb6fd52660e93f51da918972bd407367b.jpg",
		"http://f.hiphotos.baidu.com/image/pic/item/8601a18b87d6277f2b3a58322a381f30e924fc94.jpg",
		"http://h.hiphotos.baidu.com/image/pic/item/d6ca7bcb0a46f21f1186f853f4246b600c33ae04.jpg",
		"http://e.hiphotos.baidu.com/image/pic/item/2fdda3cc7cd98d107d68c538233fb80e7bec907f.jpg",
		"http://b.hiphotos.baidu.com/image/pic/item/77c6a7efce1b9d1634356c61f1deb48f8d5464c4.jpg",
		"http://b.hiphotos.baidu.com/image/pic/item/024f78f0f736afc328b5af65b119ebc4b7451270.jpg",
		"http://e.hiphotos.baidu.com/image/pic/item/29381f30e924b899651402a26c061d950a7bf604.jpg",
		"http://b.hiphotos.baidu.com/image/pic/item/e4dde71190ef76c65e705e2b9f16fdfaaf516714.jpg",
		"http://b.hiphotos.baidu.com/image/pic/item/adaf2edda3cc7cd9b4f33fd83b01213fb90e91d0.jpg",
		"http://e.hiphotos.baidu.com/image/pic/item/7a899e510fb30f241e675003ca95d143ad4b0334.jpg",
		"http://h.hiphotos.baidu.com/image/pic/item/d53f8794a4c27d1e553f47c519d5ad6eddc43891.jpg",
		"http://a.hiphotos.baidu.com/image/pic/item/377adab44aed2e733b4ded268501a18b87d6fa10.jpg",
		"http://c.hiphotos.baidu.com/image/pic/item/6f061d950a7b0208b96254eb60d9f2d3572cc815.jpg",
		"http://f.hiphotos.baidu.com/image/pic/item/2f738bd4b31c8701b40e7bef257f9e2f0708ff7b.jpg",
		"http://e.hiphotos.baidu.com/image/pic/item/9213b07eca806538a0102bf395dda144ac34825c.jpg",
		"http://c.hiphotos.baidu.com/image/pic/item/bf096b63f6246b60de1cf3fce9f81a4c510fa22c.jpg",
		"http://d.hiphotos.baidu.com/image/pic/item/8b13632762d0f7035ab2fff50bfa513d2697c593.jpg",
		"http://g.hiphotos.baidu.com/image/pic/item/09fa513d269759ee9bfbdec2b0fb43166d22df14.jpg",
		"http://c.hiphotos.baidu.com/image/pic/item/3801213fb80e7bec56f24de12d2eb9389b506b81.jpg",
		"http://g.hiphotos.baidu.com/image/pic/item/6d81800a19d8bc3ebff4aeca808ba61ea8d34535.jpg",
		"http://g.hiphotos.baidu.com/image/pic/item/8d5494eef01f3a296b7cae549b25bc315d607cf7.jpg",
		"http://c.hiphotos.baidu.com/image/pic/item/7a899e510fb30f2410264a03ca95d143ad4b0375.jpg",
		"http://e.hiphotos.baidu.com/image/pic/item/37d3d539b6003af33668a7fa372ac65c1138b65d.jpg",
		"http://g.hiphotos.baidu.com/image/pic/item/b03533fa828ba61ece63b42e4334970a304e5925.jpg",
		"http://g.hiphotos.baidu.com/image/pic/item/d62a6059252dd42a7600c6ab013b5bb5c9eab8a1.jpg",
		"http://h.hiphotos.baidu.com/image/pic/item/9358d109b3de9c822e576a8f6e81800a19d84384.jpg",
		"http://h.hiphotos.baidu.com/image/pic/item/a8ec8a13632762d05827eabfa2ec08fa513dc691.jpg",
		"http://f.hiphotos.baidu.com/image/pic/item/738b4710b912c8fc8e5db9cdfe039245d6882107.jpg",
		"http://a.hiphotos.baidu.com/image/pic/item/83025aafa40f4bfb9b4c3b54014f78f0f7361815.jpg",
		"http://h.hiphotos.baidu.com/image/pic/item/4b90f603738da9776684152bb251f8198618e303.jpg",
		"http://a.hiphotos.baidu.com/image/pic/item/c9fcc3cec3fdfc0382916c58d63f8794a5c22660.jpg",
		"http://c.hiphotos.baidu.com/image/pic/item/a6efce1b9d16fdfaadcb78e7b68f8c5494ee7b17.jpg",
		"http://c.hiphotos.baidu.com/image/pic/item/4d086e061d950a7b9022f6c408d162d9f2d3c99f.jpg",
		"http://f.hiphotos.baidu.com/image/pic/item/9358d109b3de9c82c4f95c8f6e81800a19d84315.jpg",
		"http://h.hiphotos.baidu.com/image/pic/item/38dbb6fd5266d0166e34dfa0952bd40735fa350c.jpg",
		"http://a.hiphotos.baidu.com/image/pic/item/9f510fb30f2442a7819146a5d343ad4bd1130216.jpg",
		"http://c.hiphotos.baidu.com/image/pic/item/8cb1cb1349540923df1dcb7e9058d109b3de49bd.jpg",
		"http://b.hiphotos.baidu.com/image/pic/item/b8389b504fc2d5624cfc5fcee41190ef76c66c19.jpg",
		"http://b.hiphotos.baidu.com/image/pic/item/9922720e0cf3d7cae2c2ac76f01fbe096b63a9fe.jpg",
		"http://d.hiphotos.baidu.com/image/pic/item/ac6eddc451da81cb5a7fc2455066d01608243198.jpg",
		"http://d.hiphotos.baidu.com/image/pic/item/9825bc315c6034a8fd747a6ec91349540823764f.jpg",
		"http://d.hiphotos.baidu.com/image/pic/item/0824ab18972bd4075e16927579899e510eb30960.jpg",
		"http://h.hiphotos.baidu.com/image/pic/item/8b13632762d0f703be0d5cd50afa513d2697c578.jpg",
		"http://e.hiphotos.baidu.com/image/pic/item/4ec2d5628535e5dd6321936377c6a7efce1b6278.jpg",
		"http://f.hiphotos.baidu.com/image/pic/item/cb8065380cd791236a46cb32ae345982b3b780d6.jpg",
		"http://e.hiphotos.baidu.com/image/pic/item/4d086e061d950a7b3ee598cd08d162d9f3d3c9e3.jpg",
		"http://e.hiphotos.baidu.com/image/pic/item/a5c27d1ed21b0ef45fcd235edfc451da81cb3e8c.jpg",
		"http://b.hiphotos.baidu.com/image/pic/item/d000baa1cd11728b766f2a22cafcc3cec3fd2c6b.jpg",
		"http://h.hiphotos.baidu.com/image/pic/item/e850352ac65c1038c47a33c2b0119313b07e89ae.jpg",
		"http://a.hiphotos.baidu.com/image/pic/item/f11f3a292df5e0fec91dfa005e6034a85edf72ad.jpg",
		"http://f.hiphotos.baidu.com/image/pic/item/c8177f3e6709c93da596dcef9d3df8dcd1005446.jpg",
		"http://h.hiphotos.baidu.com/image/pic/item/a8773912b31bb05187d90f28347adab44aede035.jpg",
		""
	};
}
