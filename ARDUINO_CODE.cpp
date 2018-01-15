#include <avr/interrupt.h>
volatile int temp = 0;

#define MAXA    51 //So lich trinh toi da
#define relay     12  // Pin dieu khien relay

byte hh,mm,ss,d,m=0;
byte hl,ml,sl;
int t_delay = 0;
int t_in,d_in = 0;
int min=1500;
int status [3];
boolean got_time=false;
boolean key=false;
boolean led_R = true; //Trang thai relay
int thoigian [MAXA]; // Mang luu thoi gian cac lich trinh
int time_d [MAXA+1]; // time delay (phut)
String recv[5]; 
void setup() {
	pinMode(relay, OUTPUT);
	digitalWrite(relay , 1); // relay kich muc 0
	Serial.begin(9600);
	cli();
		TCCR1A =0;
		TCCR1B =0;
		TIMSK1 =0;
		TCCR1B |= (1 << CS11) | (1 << CS10);
		TCNT1 = 40536;
		TIMSK1 = (1 << TOIE1);
	sei();
	
	status[1] = -1;
	status[2] = -1;
}
ISR (TIMER1_OVF_vect) { // Dong ho 
	TCNT1 = 40536; //16Mhz
	temp++;
	if (temp >= 10) 
	{
		inc_time();
		Calculator();
		key=true;
		temp=0;
	}
}
void DEBUG() { //Xuat mang lich trinh ve thiet bi
	for (int i=1;i<MAXA;i++)
	{
		String s= String(thoigian[i]) + ' ' + String(time_d[i]);
		Serial.println("#LOG: "+s+"~");
		delay(100);
	}
}
void inc_time() { //Tang thoi gian (giay)
	// hh mm ss d m y
	ss++;
	if (ss >= 60) { mm++;ss-=60; };
	if (mm >= 60) { hh++;mm-=60;if (t_delay >0) {t_delay--;}; }; // t_delay: Bien luu time con lai cua lan chay
	if (hh >= 24) { hh-=24;d++; };
}
void print_time() { //Xuat thoi gian hien tai tren board
	String tmp ="#time " + String(hh) + ' ' + String(mm) + ' ' + String(ss) + "~";
    Serial.println(tmp);
}
void SYNC(byte H,byte M,byte S) { //Cai dat thoi gian vao board
	hh = H;
	mm = M;
	ss = S;
	Calculator();
	Serial.println("#LOG: Da dong bo thoi gian!~");
}
void CHECK() { //Xuat trang thai cua board
  Calculator();
  String s="";
  if (status[1] == -1)  // khong dang chay lich ttrinh nao
  {
    if (status[2] == -1) // hetlich trinh ngay hom nay
    {
      s = "Het lich trinh ngay hom nay!";
    } else s =" Con: " + String(get_hh(min)) + " gio " + String(get_mm(min)) + " phut!";
  } else 
  {
    int a = status[1];
    int tg = thoigian[a] + time_d[a] - hh*60 -mm;
    s = "Tiep tuc hoat dong trong " + String(get_hh(tg)) + " gio " + String(get_mm(tg)) + " phut!";
  };
  
	Serial.println("#LOG: "+s+"~");
}
void SET(int H,int M,int T) { //*Them lich trinh
	int tg1,tg2,tg3;
	tg1 =H;tg2=M;tg3=T;
	t_in=tg1*60 + tg2;
  d_in=tg3;
	for (int i=1;i<MAXA;i++)
	{
		if (time_d[i] == 0)
		{
			thoigian[i]=t_in;
			time_d[i]=d_in;
			break;
		}
	}
}
void CLEAR() { //Xoa toan bo lich trinh
	for (int i=1;i<MAXA;i++) if (time_d[i] > 0)
	{
		thoigian[i]=0;
		time_d[i]=0;
	}
	Serial.println("#LOG: Xoa toan bo lich trinh~");
}
void GET() {
	Serial.println("#xoahet~");
	delay(100);
	// Xuat tung goi du lieu ra Serial
	for (int i=1;i<MAXA;i++) if (time_d[i] > 0) {
		String tmp = String(get_hh(thoigian[i])) +" " + String(get_mm(thoigian[i])) + " " + String(time_d[i]);
		Serial.println("#add " + tmp + "~");
		delay(90); //
	}
	Serial.println("#LOG: Xuat du lieu~");
	delay(50);
}
void Calculator() { //Dong bo du lieu
	status[1] = get_cr_schedule();
	if (status[1] > 0) 
	{
		if (led_R ==false) {
			led_R=true;
			digitalWrite(relay ,!led_R);
		}
	} else if (status[1] == -1) 
	{
		if (led_R ==true) {
			led_R=false;
			digitalWrite(relay ,!led_R);
		}
		status[2] = get_near_schedule();
	}
}
void checkRecv() { //Kiem tra du lieu nhan ve tu cong ket noi
  int k=0;
  byte a;
	while (Serial.available() > 0) if (Serial.peek() != 35) {a= Serial.read();} else break;
    if (Serial.peek() == 35)  // ki tu "#"
    {
		byte tmp = Serial.read(); // loai bo ki tu #
		while (Serial.available() > 0)
		{
			if (Serial.peek() == 32) // ki tu " " trong bang ma ascii
			{
				if (recv[k].equals("")==false) k++;
				tmp = Serial.read();
				continue;
			}
			if (Serial.peek() == 126) //ki tu "~"
			{
				tmp = Serial.read();
				break;
			} else recv[k] =recv[k] + String((char)Serial.read());
		}
	}
		if (recv[0].equals("sync"))   SYNC(byte(recv[1].toInt()),byte(recv[2].toInt()),byte(recv[3].toInt())); 
		if (recv[0].equals("set"))    SET(int(recv[1].toInt()),int(recv[2].toInt()),int(recv[3].toInt()));  
		if (recv[0].equals("get"))    GET();  
		if (recv[0].equals("check"))  CHECK();
		if (recv[0].equals("clear"))  CLEAR();
		if (recv[0].equals("time"))   print_time();
		if (recv[0].equals("bug"))    DEBUG();
		if (recv[0].equals("sapxep")) sapxep();
	for(int i=0;i<=9;i++) recv[i]="";
}
void loop() {
	if (Serial.available() > 0){ 
	delay(70); checkRecv();}
	
    if (key == true)
    {
    	key=false;
    	//print_time();	
		//CHECK();
    }
    delay(30);
}
int get_cr_schedule() { //Kiem tra xem co ich trinh hien tai khong
	int crtime = hh*60+mm;
	for (byte i=1;i<MAXA;i++) if (time_d[i] > 0) if ((thoigian[i] <= crtime) && (crtime - thoigian[i] < time_d[i])) return i;
	return -1;
}
int get_near_schedule() { //Kiem tra lich trinh ke tiep trong ngay
	int crtime = hh*60+mm;
	min=1500; int vt = -1;
	for (byte i=1;i<MAXA;i++) if (time_d[i] > 0) if (thoigian[i] > crtime) if (thoigian[i] - crtime < min) 
	{
		min= thoigian[i] - crtime;
		vt=i;
	}
	return vt;
}
int get_hh(int k) {
  int tg = k/60;
  return tg;
}
int get_mm(int k) {
  int tg = k/60;
  tg = k-tg*60;
  return tg;
}
void sapxep() {
	int tg1,tg2;
	for (int i=1;i<MAXA;i++) if (time_d[i] == 0) thoigian[i]=0;
	for (int i = 1; i < MAXA; i++)
		for (int j=i+1; j <=MAXA; j++)
			if (thoigian[i] < thoigian[j]) {
				tg1= thoigian[i];tg2 = time_d[i];
				thoigian[i]=thoigian[j];
				time_d[i]=time_d[j];
				thoigian[j]=tg1;
				time_d[j]=tg2;
			}
}
