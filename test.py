import codecs
name = ["Apple___Apple_scab",
"Apple___Black_rot",
"Apple___Cedar_apple_rust",
"Cherry_(including_sour)___Powdery_mildew",
"Corn_(maize)___Cercospora_leaf_spot",
"Corn_(maize)___Common_rust_",
"Corn_(maize)___Northern_Leaf_Blight",
"Grape___Black_rot",
"Grape___Esca_(Black_Measles)",
"Grape___Leaf_blight_(Isariopsis_Leaf_Spot)",
"Orange___Haunglongbing_(Citrus_greening)",
"Peach___Bacterial_spot",
"Pepper,_bell___Bacterial_spot",
"Potato___Early_blight",
"Potato___Late_blight",
"Squash___Powdery_mildew",
"Strawberry___Leaf_scorch",
"Tomato___Bacterial_spot",
"Tomato___Early_blight",
"Tomato___Late_blight",
"Tomato___Leaf_Mold",
"Tomato___Septoria_leaf_spot",
"Tomato___Spider_mites",
"Tomato___Target_Spot",
"Tomato___Tomato_Yellow_Leaf_Curl_Virus",
"Tomato___Tomato_mosaic_virus"
]
f = codecs.open('info.txt','r','utf-8')
lines = [line.strip() for line in f] 
# print(lines[-1])
state = False
content = []
idx = 0
for line in lines:
    # print(line)
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