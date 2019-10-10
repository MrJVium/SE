import sys
result=[]
with open('subway.json', encoding='utf-8-sig') as f:
	for line in f:
		result.append(list(line.strip('\n').split(',')))
res = {}
for i in result:
    if i[0] in res:
        res[i[0]].append(i[1])
    else :
        res[i[0]] = [i[1]]

with open('subway.txt', 'w') as f:
    f.write("[\n")
    for key in res:
        f.write("\t{\n")
        f.write("\t\t'Line' : '" + key + "',\n")
        f.write("\t\t'Station' : [")
        flag = 0
        for i in res[key]:
            if flag == 1:
                f.write(', ')
            else :
                flag = 1
            f.write("'" + i + "'")
        f.write(']\n\t},\n')
    f.write("]")
    f.close()