
content = []
idx = 0
for line in lines:
    
    if line == "流行规律":
        state = False
        file = codecs.open(".\ChemPrevention\\" + name[idx] + "_ChemPrevention" + '.txt','w','utf-8')
        content.pop()
        file.write("".join(content))
        content = []
        idx += 1
    if state == True:
        line = line + '\n'
        content.append(line)

    if line == "化学防治":
        state = True
file = codecs.open(".\ChemPrevention\\" + name[idx] + "_ChemPrevention" + '.txt','w','utf-8')
        
file.write("".join(content))