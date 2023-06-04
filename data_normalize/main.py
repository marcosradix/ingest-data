import pandas as pd
import re


df = pd.read_excel('data.xls')

df_normalized = df.drop(df.columns[[1,2,3,4,6,7,8,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35, 37,38,39,40,41,42,43,44,45]], axis=1)
df_normalized = df_normalized.set_axis(['Name', 'BarCode', 'Description', 'Price'], axis=1)

df_normalized["Price"]=df_normalized["Price"].str.replace(',','.')
df_normalized['Price'] = df_normalized['Price'].astype(float)
df_normalized['BarCode'] = df_normalized['BarCode'].astype(str)

df_normalized["Name"]=df_normalized["Name"].str.replace('\r', '').replace('\n', '')
df_normalized["Description"]=df_normalized["Description"].str.replace('\r', '').replace('\n', '')

df_normalized["Name"]=df_normalized["Name"].apply(lambda x: re.sub(r'"', '', x))
df_normalized["Description"]=df_normalized["Description"].apply(lambda x: re.sub(r'"', '', x))

#df_normalized = df_normalized.drop(df_normalized.loc[df_normalized["Price"].empty], axis=1)
df_normalized.drop(df_normalized.loc[df_normalized["Price"].isnull()], axis=1)
print(df_normalized.head())

df_normalized.to_csv("data_normalized.csv", index=False, sep="#")