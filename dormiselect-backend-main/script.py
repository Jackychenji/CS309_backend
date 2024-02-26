import requests

ENDPOINT = 'http://10.26.135.4:8080/api/'
USER = '12430284'
PASSWORD = ''

s = requests.Session()

if __name__ == '__main__':
    s.get(ENDPOINT + 'hello')

    login_res = s.post(ENDPOINT + 'login', json={'username': USER, 'password': PASSWORD}).json()
    if login_res['code'] != 200:
        raise Exception(login_res['message'])

    current_team_res = s.get(ENDPOINT + 'student/team/info').json()
    team = current_team_res['data']
    if not team:
        print('创建队伍')
        team_create_res = s.post(ENDPOINT + 'student/team/create',
                                 json={'name': USER + '的队伍', 'maxSize': 1, 'recruiting': False,
                                       'introduction': ''}).json()
        if team_create_res['code'] != 200:
            raise Exception(team_create_res['message'])

        current_team_res = s.get(ENDPOINT + 'student/team/info').json()
        team = current_team_res['data']
    elif team['leader']['studentId'] != int(USER):
        print('你不是你所在队伍的队长，无法抢宿舍')
        exit(1)
    elif len(team['members']) != team['maxSize']:
        print('队伍人数不足，无法抢宿舍')
        exit(1)

    gender = team['leader']['gender']
    size = team['maxSize']
    print('性别：', gender)

    print('获取宿舍列表')
    dorm_list_res = s.get(ENDPOINT + 'student/dormitory/list',
                          params={'page': 1, 'pageSize': 9999999, 'zoneId': 0, 'buildingId': 0, 'size': size}).json()
    if dorm_list_res['code'] != 200:
        raise Exception(dorm_list_res['message'])
    dorm_ids = set(map(lambda x: x['id'], filter(lambda x: x['gender'] == gender, dorm_list_res['data']['rows'])))
    print('可选宿舍列表：', dorm_ids)

    input('按下回车开始抢宿舍')
    while True:
        for dorm in dorm_ids:
            select_res = s.post(ENDPOINT + 'student/team/select-dormitory', json={'id': dorm}).json()
            if select_res['code'] == 200:
                print('抢到啦！宿舍号：', dorm)
                break

            print('抢宿舍失败，宿舍号：', dorm, '，原因：', select_res['message'])
