# # -*- coding: utf8 -*-
import numpy as np
from scipy import sparse
import io


def LoadData():
	# Demon1, Demi-Human1, Formless1
	f = io.open('Demi-Human1.txt','r', encoding='utf8')

	# temp = f.readline()
	# print temp.encode('utf8').strip('\n')
	mons_list = list()
	item_list = list()

	for line in f:
		mons_list.append(line.encode('utf8').strip('\n'))
		if line in '\n':
			# print 'yes'
			break

	for line in f:
		item_list.append(line.encode('utf8').strip('\n'))
		if line in '\n':
			# print 'yes'
			break

	mons_num = len(mons_list)
	item_num = len(item_list)
	M = np.zeros([mons_num,item_num])

	# print np.zeros([mons_num,item_num])
	# print M.shape

	temp_list = list()

	for line in f:
		position =  (line.encode('utf8').strip('\n')).split()
		# print position[0]
		M[int(position[0])][int(position[1])] = 1

	print item_list[2]
	return sparse.coo_matrix(M), mons_num, item_num, item_list, mons_list

LoadData()




