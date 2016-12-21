from Tkinter import *
from scipy import sparse
import numpy as np
import random
import loadMatrix

def FindBiggestN(data, N=1):
    biggestN = list()
    count = 0
    copy_data = np.array(data)
    for i in range(len(copy_data)):
        if count < N:
            index = np.argmax(copy_data)
            biggestN.append(index)
            copy_data[index] = 0
            count = count +1
    return biggestN

def Gen_rand_sparse_mat(m, n, density):
    M = sparse.rand(m, n, density=density)
    M.data[:] = 1
    return M

##Greedy
def QuestionSellectionAlgo_greedy(X, w, qs):
    X_qs = X.toarray()[:, qs]
    # print X_qs
    E = np.dot(w, X_qs) / ITEM_COUNT
    q = np.argmin(np.fabs(E - 0.5))
    return qs[q]

def jump_prob(e, enew, T):
    if enew < e: 
        return 1
    else:
        return np.exp((e-enew)/T)

def compute_min_d(X_qk, GAMMA, w, ls):
    ls_ds = []
    for l in ls:
        dcs = [np.bitwise_xor(l, X_qk.astype(int)).sum()]
        d = (w*[1 - GAMMA**dc for dc in dcs]).sum()
        ls_ds.append(d)
    return np.min(ls_ds)

#ihs_sa
def questionSellectionAlgo_ihs_sa(X, GAMMA, w, qs, k, I, T=100):
    ls = (((np.array(range(2**k))[:,None] & (1 << np.arange(k)))) > 0).astype(int)
    qk = np.random.choice(qs, k, replace=False)
    X_qk = X.toarray()[:, qk]
    min_d = compute_min_d(X_qk, GAMMA, w, ls)
    t = T
    
    qk_best = qk
    for i in range(I):
        qk_n = np.array(qk)
        qk_n[np.random.choice(k)] = np.random.choice(np.setdiff1d(qs, qk))
        X_qk_n = X.toarray()[:, qk_n]
        min_d_n = compute_min_d(X_qk_n, GAMMA, w, ls)
        
        t = t*0.9
        if jump_prob(min_d, min_d_n, t) > np.random.rand(1):
            qk = qk_n
        if min_d_n > min_d:
            min_d = min_d_n
            qk_best = qk_n
    return qk_best

def InteractiveRecommandation(X, T, GAMMA, ans, w, r_qs, algr, q_array=None):
    if algr == 0:
        q = QuestionSellectionAlgo_greedy(X, w, r_qs)
        # def QuestionSellectionAlgo_greedy(X, w, qs):
    elif algr == 1:
        q = questionSellectionAlgo_ihs_sa(X, GAMMA, w, r_qs, 2, ITEM_COUNT)[0]
        # def questionSellectionAlgo_ihs_sa(X, GAMMA, w, qs, k, I, T):
    elif algr == 2:
        if len(q_array) == 0:
            q_array = questionSellectionAlgo_ihs_sa(X, GAMMA, w, r_qs, 2, ITEM_COUNT)
            q = q_array[0]
            q_array = np.delete(q_array , 0)
        else:
            q = q_array[0]
            q_array = np.delete(q_array , 0)

    #delete index
    r_qs = r_qs[r_qs != q]
    X_q = X.toarray()[:, q]
    # print 'select', TAGS[q], ', X(q): ', X_q
    
    g = np.ones(ITEM_COUNT)
    if ans == '1':
        g = X_q
    elif ans == '0':
        g = (1 - X_q)

    #tag_g == 0, set decrease
    g[g == 0] = GAMMA
    #decrease weight
    w = w*g
    if algr == 0 or algr == 1:
        return w, r_qs, TAGS[q]
    else:
        return w, r_qs, TAGS[q], q_array

class GUIDemo(Frame):
    def __init__(self, master=None):
        Frame.__init__(self, master)
        self.grid()
        self.createWidgets()
        
    def createWidgets(self):
        #attribute init
        self.algr = 0
        self.r_qs = np.array(range(TAG_COUNT)) #remain questions: had not asked
        self.w = np.ones(ITEM_COUNT)
        self.tag = ''
        self.shsList = list()

        #Choose Algorithm Label
        self.chooseAlgrLabel = Label(self)
        self.chooseAlgrLabel["text"] = "Algorithm:"
        self.chooseAlgrLabel.grid(row=0, column=0, sticky='w')

        #SHS Button
        self.shsButton = Button(self)
        self.shsButton["text"] = "SHS"
        self.shsButton.grid(row=0, column=0, sticky='e')
        self.shsButton['command'] =  lambda: self.setGreedy(2)

        #Greedy Button
        self.greedyButton = Button(self)
        self.greedyButton["text"] = "Greedy"
        self.greedyButton.grid(row=0, column=1, sticky='w')
        self.greedyButton['command'] = lambda: self.setGreedy(0)

        #IHS Button
        self.ihsButton = Button(self)
        self.ihsButton["text"] = "IHS"
        self.ihsButton.grid(row=0, column=1, sticky='e')
        self.ihsButton['command'] =  lambda: self.setGreedy(1)

        #Algorithm Label
        self.AlgrLabel = Label(self)
        if self.algr == 0:
            self.AlgrLabel["text"] = "Greedy"
        elif self.algr ==1:
            self.AlgrLabel['text'] = "IHS"
        else:
            self.AlgrLabel['text'] = 'SHS'
        self.AlgrLabel.grid(row=0, column=2)

    	#Label ask Q_num
        self.inputText = Label(self)
        self.inputText["text"] = "Max Question Number:"
        self.inputText.grid(row=1, column=0)

        #input entry
        self.inputField = Entry(self)
        self.inputField["width"] = 15
        self.inputField.grid(row=1, column=1)
        
        #Start Button
        self.startButton = Button(self)
        self.startButton['text'] = "Start!"
        self.startButton.grid(row=1, column=2, sticky='w')
        self.startButton["command"] = self.startMethod
        
        #Question Label
        self.questionLabel = Label(self)
        self.questionLabel["text"] = "Question:"
        self.questionLabel.grid(row=2, column=0, sticky='e')

        #Question Appear Label
        self.questionAppearLabel = Label(self)
        self.questionAppearLabel["text"] = "Wait for start"
        self.questionAppearLabel.grid(row=2, column=1, sticky='w')
        
        #Answer Label
        self.answerLabel = Label(self)
        self.answerLabel["text"] = "Answer:"
        self.answerLabel.grid(row=3, column=0, sticky='e')
        
        #Yes Button
        self.yesButton = Button(self)
        self.yesButton["text"] = "Yes"
        self.yesButton.grid(row=3, column=1, sticky='w')
        self.yesButton["command"] = self.yesMethod
        
        #No Button
        self.noButton = Button(self)
        self.noButton["text"] = "No"
        self.noButton.grid(row=3, column=1)
        self.noButton["command"] = self.noMethod
        
        #leftTimes Label
        self.leftTimesLabel = Label(self)
        self.leftTimesLabel["text"] = "Times left:"
        self.leftTimesLabel.grid(row=3, column=0, sticky='w')

        #Guessing Label
        self.guessingLabel = Label(self)
        self.guessingLabel["text"] = "Are you thinking about:"
        self.guessingLabel.grid(row=4, column=0, sticky='e')

        #Guessing Appear Label
        self.guessingAppearLabel = Label(self)
        self.guessingAppearLabel["text"] = "Wait for answer..."
        self.guessingAppearLabel.grid(row=4, column=1, sticky='w')

		#Reset Button
        self.resetButton = Button(self)
        self.resetButton["text"] = "Reset"
        self.resetButton.grid(row=4, column=2)
        self.resetButton['command'] = self.resetMethod

		#Cutline Label
        self.cutLineLabel = Label(self)
        self.cutLineLabel["text"] = "----------------------Ranking below---------------------"
        self.cutLineLabel.grid(row=5, column=0, columnspan=3)

        #Rank1 Label
        self.rank1Label = Label(self)
        self.rank1Label['text'] = '1.'
        self.rank1Label.grid(row=6, column=0, sticky='w')

        #Rank2 Label
        self.rank2Label = Label(self)
        self.rank2Label['text'] = '2.'
        self.rank2Label.grid(row=7, column=0, sticky='w')

        #Rank3 Label
        self.rank3Label = Label(self)
        self.rank3Label['text'] = '3.'
        self.rank3Label.grid(row=8, column=0, sticky='w')

        #Rank4 Label
        self.rank4Label = Label(self)
        self.rank4Label['text'] = '4.'
        self.rank4Label.grid(row=9, column=0, sticky='w')

        #Rank5 Label
        self.rank5Label = Label(self)
        self.rank5Label['text'] = '5.'
        self.rank5Label.grid(row=10, column=0, sticky='w')

        #for test 
        # self.displayText = Label(self)
        # self.displayText["text"] = "something happened"
        # self.displayText.grid(row=3, column=0, columnspan=7)

    def startMethod(self):
    	question_num = self.inputField.get()
        self.leftTimesLabel['text'] = 'Times left:' + question_num
        self.startButton["state"] = 'disabled'
        self.inputField["state"] = 'disabled'
        self.T = int(question_num)

        if self.algr == 0 or self.algr == 1:
            # use 3 to not decrease W
            self.w, self.r_qs, self.tag = InteractiveRecommandation(X, self.T, GAMMA, 3, self.w, self.r_qs, self.algr)
        elif self.algr == 2:
            self.w, self.r_qs, self.tag, self.shsList = InteractiveRecommandation(X, self.T, GAMMA, 3, self.w, self.r_qs, self.algr, self.shsList)
        self.updateLabel()

    def resetMethod(self):
        self.startButton["state"] = 'normal'
        self.inputField["state"] = 'normal'
        self.leftTimesLabel['text'] = 'Times left:'
        self.yesButton['state'] = 'normal'
        self.noButton['state'] = 'normal'
        self.rank1Label['text'] = '1.'
        self.rank2Label['text'] = '2.'
        self.rank3Label['text'] = '3.'
        self.rank4Label['text'] = '4.'
        self.rank5Label['text'] = '5.'

    def yesMethod(self):
        self.T = self.T - 1
        self.leftTimesLabel['text'] = 'Times left:' + str(self.T)
        ans = '1'
        
        if self.algr == 0 or self.algr == 1:
            self.w, self.r_qs, self.tag = InteractiveRecommandation(X, self.T, GAMMA, ans, self.w, self.r_qs, self.algr)
            # self.updateLabel()
        elif self.algr == 2:
            self.w, self.r_qs, self.tag, self.shsList = InteractiveRecommandation(X, self.T, GAMMA, ans, self.w, self.r_qs, self.algr, self.shsList)
            # self.updateLabel()
        self.updateLabel()
        if self.T == 0:
            self.yesButton['state'] = 'disabled'
            self.noButton['state'] = 'disabled'
        print self.w

    def noMethod(self):
        self.T = self.T - 1
        self.leftTimesLabel['text'] = 'Times left:' + str(self.T)
        ans = '0'

        if self.algr == 0 or self.algr == 1:
            self.w, self.r_qs, self.tag = InteractiveRecommandation(X, self.T, GAMMA, ans, self.w, self.r_qs, self.algr)
        elif self.algr == 2:
            self.w, self.r_qs, self.tag, self.shsList = InteractiveRecommandation(X, self.T, GAMMA, ans, self.w, self.r_qs, self.algr, self.shsList)
        self.updateLabel()
        #configure button
        if self.T == 0:
            self.yesButton['state'] = 'disabled'
            self.noButton['state'] = 'disabled'
        print self.w

    def updateLabel(self):
        biggestN = FindBiggestN(self.w, 5)
        self.rank1Label['text'] = '1.' + 'item_' + str(biggestN[0])
        self.rank2Label['text'] = '2.' + 'item_' + str(biggestN[1])
        self.rank3Label['text'] = '3.' + 'item_' + str(biggestN[2])
        self.rank4Label['text'] = '4.' + 'item_' + str(biggestN[3])
        self.rank5Label['text'] = '5.' + 'item_' + str(biggestN[4])
        self.questionAppearLabel['text'] = 'Does item have TAG:' + self.tag

    def setGreedy(self, algr):
        self.algr = algr
        if self.algr == 0:
            self.AlgrLabel['text'] = 'Greedy'
        elif self.algr ==1:
            self.AlgrLabel['text'] = 'IHS'
        else:
            self.AlgrLabel['text'] = 'SHS'

if __name__ == '__main__':
	### Define parameter
    TAGS = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J']
    ITEM_COUNT = 20
    TAG_COUNT = len(TAGS)
    DENSITY = 0.15
    MONS = list()
    ### Data preparation
    # X: tag matrix
    # T: maximun number of questions
    # GAMMA: discount factor
    X = Gen_rand_sparse_mat(ITEM_COUNT, TAG_COUNT, DENSITY)
    # X, ITEM_COUNT, TAG_COUNT, TAGS, MONS = loadMatrix.LoadData()
    T = 0
    GAMMA = 0.75

    root = Tk()
    root.title("Data Mining Final Project")
    root.resizable(0,0)
    app = GUIDemo(master=root)
    app.mainloop()


