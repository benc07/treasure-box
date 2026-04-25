import json
from flask import Flask, request
import db
DB=db.DatabaseDriver()
app=Flask(__name__)
@app.route("/")
def hello_world():
   return "Hello world!"


# treasure box routes




# Returns all treasure boxes (no transactions nested).
@app.route("/api/boxes/", methods=["GET"])
def get_all_boxes():
   return json.dumps({"boxes":DB.get_all_boxes()}),200


# Returns a single treasure box including its full transaction history.
@app.route("/api/boxes/<int:box_id>/", methods=["GET"])
def get_box(box_id):
   box=DB.get_box_by_id(box_id)
   if box is None:
       return json.dumps({"error":"Treasure box not found"}),404
   return json.dumps(box),200


# Creates a new treasure box.
# required fields are name, target_amount
# Optional fields are collaborators (list), cover_image (string URL)
@app.route("/api/boxes/", methods=["POST"])
def create_box():
   body=json.loads(request.data)
   name=body.get("name")
   target_amount=body.get("target_amount")
   collaborators=body.get("collaborators",[])
   cover_image=body.get("cover_image",None)
   if not name:
       return json.dumps({"error":"Missing required field: name"}),400
   if target_amount is None:
       return json.dumps({"error":"Missing required field: target_amount"}),400
   if target_amount<0:
       return json.dumps({"error":"target_amount must be nonnegative"}),400
   box=DB.create_box(name,target_amount,collaborators,cover_image)
   return json.dumps(box),201


# Deletes a treasure box and all of its transactions.
@app.route("/api/boxes/<int:box_id>/", methods=["DELETE"])
def delete_box(box_id):
   box=DB.get_box_by_id(box_id)
   if box is None:
       return json.dumps({"error":"Treasure box not found"}),404
   DB.delete_box_by_id(box_id)
   return json.dumps(box),200
#  TRANSACTION ROUTES

# Creates a deposit or withdrawal transaction for a specific box.
@app.route("/api/boxes/<int:box_id>/transactions/", methods=["POST"])
def create_transaction(box_id):
   body=json.loads(request.data)
   type=body.get("type")
   label=body.get("label")
   amount=body.get("amount")
   note=body.get("note",None)
   created_by=body.get("created_by",None)
   if not type:
       return json.dumps({"error":"Missing required field: type"}),400
   if not label:
       return json.dumps({"error":"Missing required field: label"}),400
   if amount is None:
       return json.dumps({"error":"Missing required field: amount"}),400
   transaction,status_code,error=DB.create_transaction(
       box_id,type,label,amount,note,created_by
   )
   if error is not None:
       return json.dumps({"error":error}),status_code
   return json.dumps(transaction),status_code
if __name__ == "__main__":
   app.run(host="0.0.0.0",port=5000,debug=True)