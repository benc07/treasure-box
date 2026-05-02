import sqlite3
from datetime import datetime

def singleton(cls):
   instances={}
   def getinstance():
       if cls not in instances:
           instances[cls]=cls()
       return instances[cls]
   return getinstance
class DatabaseDriver(object):
   """
   Database driver for the Shared Treasure Box app.
   Handles reading and writing data with the database.
   """
   def __init__(self):
       """Secures connection with database."""
       self.conn=sqlite3.connect(
           "treasurebox.db",check_same_thread=False
       )
       self.create_boxes_table()
       self.create_transactions_table()


   #  TABLE CREATION
   def create_boxes_table(self):
       """Create treasure_boxes table if it does not exist."""
       self.conn.execute("""
           CREATE TABLE IF NOT EXISTS treasure_boxes (
               id            INTEGER PRIMARY KEY AUTOINCREMENT,
               name          TEXT    NOT NULL,
               target_amount REAL    NOT NULL DEFAULT 0,
               balance       REAL    NOT NULL DEFAULT 0,
               cover_image   TEXT,
               collaborators TEXT,
               created_at    TEXT    NOT NULL
           );
       """)
       self.conn.commit()
   def create_transactions_table(self):
       """Create transactions table if it does not exist.
          Has a FOREIGN KEY relationship to treasure_boxes.
       """
       self.conn.execute("""
           CREATE TABLE IF NOT EXISTS transactions (
               id         INTEGER PRIMARY KEY AUTOINCREMENT,
               box_id     INTEGER NOT NULL,
               type       TEXT    NOT NULL,
               label      TEXT    NOT NULL,
               amount     REAL    NOT NULL,
               note       TEXT,
               created_by TEXT,
               timestamp  TEXT    NOT NULL,
               FOREIGN KEY(box_id) REFERENCES treasure_boxes(id)
           );
       """)
       self.conn.commit()


   #  serialize functions
  
   def serialize_box(self,row):
       """Convert a treasure_box row into a dictionary."""
       collaborators=row[5].split(",") if row[5] else []
       return {
           "id":row[0],
           "name":row[1],
           "target_amount":row[2],
           "balance":row[3],
           "cover_image":row[4],
           "collaborators":collaborators,
           "created_at":row[6],
       }
   def serialize_transaction(self,row):
       """Convert a transaction row into a dictionary."""
       return {
           "id":row[0],
           "box_id":row[1],
           "type":row[2],
           "label":row[3],
           "amount":row[4],
           "note":row[5],
           "created_by":row[6],
           "timestamp":row[7],
       }
   #treasure box methods
   def get_all_boxes(self):
       """Return all treasure boxes (without transactions)."""
       cursor=self.conn.execute(
           "SELECT id, name, target_amount, balance, cover_image, collaborators, created_at FROM treasure_boxes;"
       )
       return [self.serialize_box(row) for row in cursor]
   def get_box_by_id(self,box_id):
       """Return a single treasure box by id, including its transactions."""
       cursor=self.conn.execute(
           "SELECT id, name, target_amount, balance, cover_image, collaborators, created_at FROM treasure_boxes WHERE id=?;",
           (box_id,)
       )
       row=cursor.fetchone()
       if row is None:
           return None
       box=self.serialize_box(row)
       box["transactions"]=self.get_transactions_by_box_id(box_id)
       return box
   def create_box(self,name,target_amount,collaborators,cover_image):
       """Insert a new treasure box and return it."""
       created_at=str(datetime.now())
       collaborators_str=",".join(collaborators) if collaborators else ""
       cursor=self.conn.execute(
           """
           INSERT INTO treasure_boxes (name, target_amount, balance, cover_image, collaborators, created_at)
           VALUES (?, ?, 0, ?, ?, ?);
           """,
           (name,target_amount,cover_image,collaborators_str,created_at)
       )
       self.conn.commit()
       return self.get_box_by_id(cursor.lastrowid)
   def delete_box_by_id(self,box_id):
       """Delete a treasure box and all its transactions by id."""
       self.conn.execute("DELETE FROM transactions WHERE box_id=?;",(box_id,))
       self.conn.execute("DELETE FROM treasure_boxes WHERE id=?;",(box_id,))
       self.conn.commit()
   def update_box_balance(self,box_id,new_balance):
       """Update the balance of a treasure box."""
       self.conn.execute(
           "UPDATE treasure_boxes SET balance=? WHERE id=?;",
           (new_balance,box_id)
       )
       self.conn.commit()
   #transaction methods
   def get_transactions_by_box_id(self,box_id):
       """Return all transactions for a given box, oldest first."""
       cursor=self.conn.execute(
           """
           SELECT id, box_id, type, label, amount, note, created_by, timestamp
           FROM transactions
           WHERE box_id=?
           ORDER BY id ASC;
           """,
           (box_id,)
       )
       return [self.serialize_transaction(row) for row in cursor]
   def create_transaction(self,box_id,type,label,amount,note,created_by):
       """
       Create a deposit or withdrawal transaction.
       Updates the box balance accordingly.
       Returns (transaction, status_code, error_message).
       """
       box=self.get_box_by_id(box_id)
       if box is None:
           return None,404,"Treasure box not found"
       if amount<=0:
           return None,400,"Amount must be greater than 0"
       if type=="deposit":
           new_balance=box["balance"]+amount
       elif type=="withdraw":
           if box["balance"]<amount:
               return None,403,"Insufficient funds in the box"
           new_balance=box["balance"]-amount
       else:
           return None,400,"type must be 'deposit' or 'withdraw'"
       self.update_box_balance(box_id,new_balance)
       timestamp=str(datetime.now())
       cursor=self.conn.execute(
           """
           INSERT INTO transactions (box_id, type, label, amount, note, created_by, timestamp)
           VALUES (?, ?, ?, ?, ?, ?, ?);
           """,
           (box_id,type,label,amount,note,created_by,timestamp)
       )
       self.conn.commit()
       transaction=self.serialize_transaction((
           cursor.lastrowid,box_id,type,label,amount,note,created_by,timestamp
       ))
       return transaction,201,None


DatabaseDriver=singleton(DatabaseDriver)
