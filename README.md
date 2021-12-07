# GPS-based-Covid-19-tracking
GPS&블루투스 기반 Covid-19 확진자 동선 파악을 위한 서비스


현재 코로나바이러스때문에 많은 사람들의 생활범위와 삶의 질을 빼앗기고 있다. 사람들은 코로나바이러스 뿐만 아니라 코로나사태 종식이후에도 많은 바이러스들에게서 위협받을 수 있다. 그래서 바이러스 확산을 확실하게 막고자, 위치기반으로 한 사용자 위치추적 시스템을 개발하였다. 감염자가 계속해서 늘어나는 위험상황에서 사용자들은 이 어플리케이션을 이용하여 감염확산을 막는데 기여할 수 있다. 본 시스템은 사용자들의 최근2주간 위치를 기반으로 확진자와 접촉이 있는지 확인한다. 사용자가 어플리케이션을 실행했다면 위치를 제공하고, 본 시스템을 사용하는 업소에 입장할 때에 번거롭게 QR체크 등을 하지 않아도 된다. 업소의 단말기를 지나갈 때 본 어플리케이션을 사용하는 중이라면 비프음을 통하여 출입 인증을 받을 수 있다. 또한 확진자가 발생하면 동선이 겹치는지 확인하고, 확진자와의 접촉이 의심되면 카카오톡 메세지로 알림톡을 받는다. 


1.설치 방법
1) GPS-based-Covid-19-tracking/GPS-based-Covid-19-tracking.apk 파일을 설치한다.
2) GPS-based-Covid-19-tracking/androidapp/CovidContackManagement_mix 프로젝트를 빌드하여 사용한다.

2.사용 방법
사용자
-위치저장
 : 자세한 위치정보를 제공하고 싶지 않을 때는 GPS끄고 사용하면 블루투스 인증방식만 사용할 수 있다.(본 어플리케이션은 포그라운드상에서 작동한다.)

-블루투스 자동 체크인
 : 블루투스를 이용한 체크인 방법을 사용하고 싶으면 체크인기능을 활성화하고 알림음이 들리면 출입할 수 있다.
블루투스 자동 체크인 기능을 활성화하고 본 서비스를 사용하는 사업장에서는 휴대폰을 꺼내지 않아도 알림음이 들리면 별도의 출입명부 작성없이 입장할 수 있다.

사업자
-사업자는 사업장의 위치를 적어서 확인버튼을 누르고, 블루투스 on토글을 켜면 사용자를 받을 수 있다. 어플리케이션을 실행한 단말기를 한명씩 들어갈 수 있는 입구에 두어서 어플리케이션 사용자인지 확인한다.
